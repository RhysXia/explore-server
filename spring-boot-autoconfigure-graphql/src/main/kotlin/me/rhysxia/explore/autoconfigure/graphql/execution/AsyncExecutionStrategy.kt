package me.rhysxia.explore.autoconfigure.graphql.execution

import graphql.ExecutionResult
import graphql.collect.ImmutableKit
import graphql.execution.*
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

/**
 * 基于内置的AsyncExecutionStrategy进行实现
 */
class AsyncExecutionStrategy(exceptionHandler: DataFetcherExceptionHandler = SimpleDataFetcherExceptionHandler()) :
    AbstractAsyncExecutionStrategy(exceptionHandler) {

    private val logger = LoggerFactory.getLogger(AsyncExecutionStrategy::class.java)

    override fun execute(
        executionContext: ExecutionContext,
        parameters: ExecutionStrategyParameters
    ): CompletableFuture<ExecutionResult> {
        val instrumentation = executionContext.instrumentation
        val instrumentationParameters = InstrumentationExecutionStrategyParameters(executionContext, parameters)
        val executionStrategyCtx = instrumentation.beginExecutionStrategy(instrumentationParameters)
        val fields = parameters.fields
        val fieldNames = fields.keySet()
        val futures: MutableList<CompletableFuture<FieldValueInfo>> = ArrayList(fieldNames.size)
        val resolvedFields: MutableList<String> = ArrayList(fieldNames.size)
        for (fieldName in fieldNames) {
            val currentField = fields.getSubField(fieldName)
            val fieldPath = parameters.path.segment(mkNameForPath(currentField))
            val newParameters = parameters
                .transform { builder: ExecutionStrategyParameters.Builder ->
                    builder.field(
                        currentField
                    ).path(fieldPath).parent(parameters)
                }
            resolvedFields.add(fieldName)
            val future = resolveFieldWithInfo(executionContext, newParameters)
            futures.add(future)
        }


        val overallResult = CompletableFuture<ExecutionResult>()
        executionStrategyCtx.onDispatched(overallResult)

        val count = AtomicInteger(0)

        futures.forEach {
            it.whenComplete { _, _ ->
                count.incrementAndGet()
            }
        }

        Async.each(futures).whenComplete { completeValueInfos: List<FieldValueInfo>?, throwable: Throwable? ->

            val handleResultsConsumer =
                handleResults(executionContext, resolvedFields, overallResult)
            if (throwable != null) {
                handleResultsConsumer.accept(null, throwable.cause)
                return@whenComplete
            }
            val executionResultFuture: List<CompletableFuture<ExecutionResult>> =
                ImmutableKit.map(
                    completeValueInfos
                ) { obj: FieldValueInfo -> obj.fieldValue }
            executionStrategyCtx.onFieldValuesInfo(completeValueInfos)
            Async.each(executionResultFuture)
                .whenComplete(handleResultsConsumer)
        }.exceptionally { ex: Throwable? ->
            // if there are any issues with combining/handling the field results,
            // complete the future at all costs and bubble up any thrown exception so
            // the execution does not hang.
            executionStrategyCtx.onFieldValuesException()
            overallResult.completeExceptionally(ex)
            null
        }

        overallResult.whenComplete { result: ExecutionResult, t: Throwable? ->
            executionStrategyCtx.onCompleted(
                result,
                t
            )
        }

        val dataLoaderRegistry = executionContext.dataLoaderRegistry

        val futuresSize = futures.size

        var start: Instant? = null
        var loggerFlag = false

        if (logger.isDebugEnabled) {
            start = Instant.now()
        }

        // 轮询发送请求，保证所有loader都被dispatch
        GlobalScope.launch {
            while (!count.compareAndSet(futuresSize, 0)) {
                delay(1)
                dataLoaderRegistry.dispatchAll()

                if (logger.isDebugEnabled && !loggerFlag) {
                    if (Instant.now().isAfter(start!!.plusSeconds(10))) {
                        logger.debug("Some DataFetchers execute too long.")
                        loggerFlag = true
                    }
                }
            }
        }

        return overallResult
    }
}