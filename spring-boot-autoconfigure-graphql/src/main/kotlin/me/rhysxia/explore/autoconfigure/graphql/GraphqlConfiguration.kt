package me.rhysxia.explore.autoconfigure.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.GraphQL
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.SubscriptionExecutionStrategy
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.execution.instrumentation.Instrumentation
import graphql.schema.*
import graphql.schema.idl.*
import graphql.schema.visibility.GraphqlFieldVisibility
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.awaitSingleOrNull
import me.rhysxia.explore.autoconfigure.graphql.annotations.*
import me.rhysxia.explore.autoconfigure.graphql.exception.GraphqlTypeException
import me.rhysxia.explore.autoconfigure.graphql.interfaces.GraphqlBatchLoader
import me.rhysxia.explore.autoconfigure.graphql.interfaces.GraphqlDataFetcherParameterResolver
import me.rhysxia.explore.autoconfigure.graphql.interfaces.GraphqlMappedBatchLoader
import org.dataloader.BatchLoader
import org.dataloader.MappedBatchLoader
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.stream.Stream
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.javaType
import kotlin.streams.toList

@DelicateCoroutinesApi
@Configuration
@EnableConfigurationProperties(GraphqlConfigurationProperties::class)
@Import(value = [GraphqlControllerConfiguration::class, GraphqlWebSocketConfiguration::class])
class GraphqlConfiguration(private val graphqlConfigurationProperties: GraphqlConfigurationProperties) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private fun getSchemaFiles(): Array<Resource> {
        val loader = Thread.currentThread().contextClassLoader

        val pathMatchingResourcePatternResolver = PathMatchingResourcePatternResolver(loader)
        return try {
            pathMatchingResourcePatternResolver.getResources(graphqlConfigurationProperties.schema.location)
        } catch (e: Exception) {
            emptyArray()
        }
    }

    private fun <T : Any> getGraphqlLoaderName(instance: T): String? {
        val graphqlLoader = AnnotationUtils.findAnnotation(instance::class.java, GraphqlLoader::class.java)
        if (graphqlLoader === null) {
            return null
        }
        val name = graphqlLoader.name
        if (name.isBlank()) {
            logger.debug(
                "BatchLoader '%s' should has a name, but be blank.", instance::class.qualifiedName
            )
            return null
        }

        return name
    }

    @Bean
    fun <ID, Entity> graphqlMappedBatchLoaderMap(
        graphqlMappedBatchLoaders: List<GraphqlMappedBatchLoader<ID, Entity>>,
    ): Map<String, MappedBatchLoader<ID, Entity>> {
        return graphqlMappedBatchLoaders.mapNotNull { graphqlMappedBatchLoader ->
            val name = getGraphqlLoaderName(graphqlMappedBatchLoader)

            if (name === null) {
                return@mapNotNull null
            }

            val loader = MappedBatchLoader<ID, Entity> {
                GlobalScope.future(Dispatchers.Unconfined) {
                    graphqlMappedBatchLoader.load(it).toList().toMap()
                }
            }
            name to loader
        }.toMap()


    }

    @Bean
    fun <ID, Entity> mappedBatchLoaderMap(
        mappedBatchLoaders: List<MappedBatchLoader<ID, Entity>>,
    ): Map<String, MappedBatchLoader<ID, Entity>> {
        return mappedBatchLoaders.mapNotNull { mappedBatchLoader ->
            val name = getGraphqlLoaderName(mappedBatchLoader)
            if (name === null) {
                return@mapNotNull null
            }
            name to mappedBatchLoader
        }.toMap()
    }

    @Bean
    fun <ID, Entity> batchLoaderMap(
        batchLoaders: List<BatchLoader<ID, Entity>>,
    ): Map<String, BatchLoader<ID, Entity>> {
        return batchLoaders.mapNotNull { batchLoader ->
            val name = getGraphqlLoaderName(batchLoader)
            if (name === null) {
                return@mapNotNull null
            }
            name to batchLoader
        }.toMap()
    }

    @Bean
    fun <ID, Entity> graphqlBatchLoaders(
        graphqlBatchLoaders: List<GraphqlBatchLoader<ID, Entity>>,
    ): Map<String, BatchLoader<ID, Entity>> {
        return graphqlBatchLoaders.mapNotNull { graphqlBatchLoader ->
            val name = getGraphqlLoaderName(graphqlBatchLoader)
            if (name === null) {
                return@mapNotNull null
            }

            val loader = BatchLoader<ID, Entity> {
                GlobalScope.future(Dispatchers.Unconfined) {
                    graphqlBatchLoader.load(it).toList()
                }
            }

            name to loader
        }.toMap()
    }

    @Bean
    fun graphQLCodeRegistry(
        ctx: ApplicationContext,
        objectMapper: ObjectMapper,
        dataFetcherParameterResolvers: List<GraphqlDataFetcherParameterResolver<*>>
    ): GraphQLCodeRegistry {
        val codeRegistry = GraphQLCodeRegistry.newCodeRegistry()
        ctx.getBeansWithAnnotation(GraphqlData::class.java).forEach {
            val bean = it.value

            val graphqlData = AnnotationUtils.findAnnotation(bean::class.java, GraphqlData::class.java)

            if (graphqlData === null) {
                return@forEach
            }

            val rootParentType = graphqlData.parentType.ifBlank { bean::class.simpleName }

            val dfeType = DataFetchingEnvironment::class.createType()

            bean::class.memberFunctions.forEach beanForEach@{ method ->
                val graphqlHandler = AnnotationUtils.findAnnotation(method.javaMethod!!, GraphqlHandler::class.java)
                if (graphqlHandler === null) {
                    return@beanForEach
                }
                val parentType = graphqlHandler.parentType.ifBlank { rootParentType }
                val fieldName = graphqlHandler.fieldName.ifBlank { method.name }

                val isSuspend = method.isSuspend
                val isFuture = method.returnType.isSubtypeOf(
                    CompletionStage::class.createType(
                        listOf(
                            KTypeProjection(
                                null, null
                            )
                        )
                    )
                )

                val callArgs: List<(dfe: DataFetchingEnvironment) -> Any?> = method.parameters.map { parameter ->
                    val type = parameter.type
                    if (parameter.kind == KParameter.Kind.INSTANCE) {
                        return@map fun(_: DataFetchingEnvironment) = bean
                    }
                    if (type.isSubtypeOf(dfeType)) {
                        return@map fun(dfe: DataFetchingEnvironment) = dfe
                    }

                    dataFetcherParameterResolvers.forEach { dfpr ->
                        if (dfpr.support(parameter)) {
                            return@map @Suppress("ReactiveStreamsUnusedPublisher")
                            fun(dfe: DataFetchingEnvironment) = dfpr.resolve(dfe, parameter)
                        }
                    }

                    val graphqlInput = parameter.findAnnotation<GraphqlInput>()
                    val name = if (graphqlInput === null) parameter.name else graphqlInput.name
                    val javaType = parameter.type.javaType
                    fun(dfe: DataFetchingEnvironment) =
                        objectMapper.convertValue(dfe.getArgument(name), javaType as Class<*>)
                }

                val isSubscription = parentType == "Subscription"

                codeRegistry.dataFetcher(FieldCoordinates.coordinates(parentType, fieldName), DataFetcher { dfe ->

                    return@DataFetcher GlobalScope.future(Dispatchers.Unconfined) {
                        val args = callArgs.map { fn -> fn(dfe) }
                            .map { arg -> if (arg is Mono<*>) arg.awaitSingleOrNull() else arg }.toTypedArray()

                        var result = (if (isSuspend) method.callSuspend(*args) else method.call(*args))

                        if (isFuture) {
                            val future = result as CompletableFuture<*>
                            result = future.await()
                        }

                        if (isSubscription) {
                            when (result) {
                                is Flow<*> -> {
                                    @Suppress(
                                        "UNCHECKED_CAST",
                                        "ReactiveStreamsUnusedPublisher"
                                    ) return@future (result as Flow<Any>).asFlux()
                                }
                                is Stream<*> -> {
                                    @Suppress("ReactiveStreamsUnusedPublisher") return@future (result).toFlux()
                                }
                                is Publisher<*> -> {
                                    return@future (result)
                                }
                                else -> {
                                    throw GraphqlTypeException("Subscription Data Fetcher has to return type Flow, Publisher or Stream.")
                                }
                            }
                        }
                        when (result) {
                            is Flow<*> -> {
                                return@future (result).toList()
                            }
                            is Stream<*> -> {
                                return@future (result).toList()
                            }
                            is Flux<*> -> {
                                return@future (result).asFlow().toList()
                            }
                            else -> {
                                return@future result
                            }
                        }
                    }
                })
            }
        }
        return codeRegistry.build()
    }


    @Bean
    fun graphql(
        codeRegistry: GraphQLCodeRegistry,
        scalars: List<GraphQLScalarType>,
        instrumentations: List<Instrumentation>,
        directives: List<SchemaDirectiveWiring>,
        dataFetcherExceptionHandler: DataFetcherExceptionHandler?,
        graphqlFieldVisibility: GraphqlFieldVisibility?
    ): GraphQL {
        val schemaParser = SchemaParser()

        val typeDefinitionRegistry = getSchemaFiles().map {
            InputStreamReader(it.inputStream, StandardCharsets.UTF_8).use { stream -> schemaParser.parse(stream) }
        }.fold(TypeDefinitionRegistry()) { a, b ->
            a.merge(b)
        }

        val schemaGenerator = SchemaGenerator()

        val runtimeWiring = RuntimeWiring.newRuntimeWiring().codeRegistry(codeRegistry)

        directives.forEach {
            val graphqlDirective = AnnotationUtils.findAnnotation(it::class.java, GraphqlDirective::class.java)
            if (graphqlDirective !== null) {
                val name = graphqlDirective.name
                if (name.isNotBlank()) {
                    runtimeWiring.directive(name, it)
                    return@forEach
                }
            }
            runtimeWiring.directiveWiring(it)
        }

        if (graphqlFieldVisibility !== null) {
            runtimeWiring.fieldVisibility(graphqlFieldVisibility)
        }

        scalars.forEach { runtimeWiring.scalar(it) }

        val chainedInstrumentation = ChainedInstrumentation(instrumentations)

        val schema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring.build())
        val graphqlBuilder = GraphQL.newGraphQL(schema).instrumentation(chainedInstrumentation)
            .subscriptionExecutionStrategy(SubscriptionExecutionStrategy())

        if (dataFetcherExceptionHandler !== null) {
            graphqlBuilder.defaultDataFetcherExceptionHandler(dataFetcherExceptionHandler)
        }

        return graphqlBuilder.build()
    }

    @Bean
    fun graphqlExecutionProcessor(
        graphql: GraphQL,
        batchLoaderMap: Map<String, BatchLoader<*, *>>,
        mappedBatchLoaderMap: Map<String, MappedBatchLoader<*, *>>,
    ) = GraphqlExecutionProcessor(graphql, batchLoaderMap, mappedBatchLoaderMap)

}
