package me.rhysxia.explore.server.exception

import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CompletableFuture

@Component
class DefaultDataFetcherExceptionHandler : DataFetcherExceptionHandler {
    private val logger: Logger = LoggerFactory.getLogger(DefaultDataFetcherExceptionHandler::class.java)

    override fun handleException(handlerParameters: DataFetcherExceptionHandlerParameters): CompletableFuture<DataFetcherExceptionHandlerResult> {
        var exception = handlerParameters.exception

        if (exception is InvocationTargetException) {
            exception = exception.targetException
        }

        logger.error(
            "Exception while executing data fetcher for ${handlerParameters.path}: ${exception.message}",
            exception
        )

        val builder =
            when (exception) {
                is AuthenticationException -> {
                    TypedGraphqlError.newUnauthenticatedBuilder()

                }
                is AuthorizationException -> {
                    TypedGraphqlError.newPermissionDeniedBuilder()

                }
                is ParameterException -> {
                    TypedGraphqlError.newBadRequestBuilder()

                }
                else -> {
                    TypedGraphqlError.newInternalErrorBuilder()
                }
            }

        val graphqlError = builder.location(handlerParameters.sourceLocation)
            .path(handlerParameters.path)
            .message("%s", exception.message)
            .build()

        val result = DataFetcherExceptionHandlerResult.newResult()
            .error(graphqlError)
            .build()

        return CompletableFuture.completedFuture(result)
    }


}

