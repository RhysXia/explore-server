package me.rhysxia.explore.autoconfigure.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.GraphQL
import graphql.execution.SubscriptionExecutionStrategy
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.execution.instrumentation.Instrumentation
import graphql.schema.*
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.future
import kotlinx.coroutines.reactor.asFlux
import me.rhysxia.explore.autoconfigure.graphql.annotations.*
import me.rhysxia.explore.autoconfigure.graphql.interfaces.GraphqlBatchLoader
import me.rhysxia.explore.autoconfigure.graphql.interfaces.GraphqlDataFetcherParameterResolver
import me.rhysxia.explore.autoconfigure.graphql.interfaces.GraphqlMappedBatchLoader
import org.dataloader.BatchLoader
import org.dataloader.MappedBatchLoader
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletionStage
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaType

@Configuration
@EnableConfigurationProperties(GraphqlConfigurationProperties::class)
@ImportAutoConfiguration(value = [GraphqlControllerConfiguration::class, WebSocketConfiguration::class])
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
    val graphqlLoader = instance::class.findAnnotation<GraphqlLoader>()
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
      val rootParentType = bean::class.findAnnotation<GraphqlData>()!!.parentType
      val dfeType = DataFetchingEnvironment::class.createType()

      bean::class.memberFunctions.forEach beanForEach@{ method ->
        val graphqlHandler = method.findAnnotation<GraphqlHandler>()
        if (graphqlHandler === null) {
          return@beanForEach
        }
        val parentType = graphqlHandler.parentType.ifBlank { rootParentType }
        val fieldName = graphqlHandler.fieldName.ifBlank { method.name }

        if (parentType.isBlank()) {
          logger.error(
            "GraphqlFetcher '%s' should have a parentType, but be blank.", method.javaClass.canonicalName
          )
        }

        val isSuspend = method.isSuspend
        val isFuture =
          method.returnType.isSubtypeOf(CompletionStage::class.createType(listOf(KTypeProjection(null, null))))

        val callArgs: List<(dfe: DataFetchingEnvironment) -> Any> = method.parameters.map { parameter ->
          val type = parameter.type
          if (parameter.kind == KParameter.Kind.INSTANCE) {
            return@map fun(_: DataFetchingEnvironment) = bean
          }
          if (type.isSubtypeOf(dfeType)) {
            return@map fun(dfe: DataFetchingEnvironment) = dfe
          }

          dataFetcherParameterResolvers.forEach { dfpr ->
            val javaType = type.javaType
            if (dfpr.support(javaType)) {
              return@map fun(dfe: DataFetchingEnvironment) = dfpr.resolve(dfe, javaType)
            }
          }

          val graphqlInput = parameter.findAnnotation<GraphqlInput>()
          val name = if (graphqlInput === null) parameter.name else graphqlInput.name
          val javaType = parameter.type.javaType
          fun(dfe: DataFetchingEnvironment) = objectMapper.convertValue(dfe.getArgument(name), javaType as Class<*>)
        }

        val isSubscription = parentType.lowercase() == "subscription"

        codeRegistry.dataFetcher(FieldCoordinates.coordinates(parentType, fieldName), DataFetcher { dfe ->

//          val graphqlParentType = dfe.parentType
//          val isSubscription =
//            if (graphqlParentType is GraphQLObjectType) graphqlParentType.name == "Subscription" else false

          val args = callArgs.map { fn -> fn(dfe) }.toTypedArray()

          if (isFuture) {
            return@DataFetcher method.call(*args)
          }

          return@DataFetcher GlobalScope.future(Dispatchers.Unconfined) {
            val result: Any? = if (isSuspend) method.callSuspend(*args) else method.call(*args)
            if (result is Flow<*>) {
              val flow = result as Flow<Any>
              if (isSubscription) {
                flow.asFlux()
              } else {
                flow.toList()
              }
            } else {
              result
            }
          }
        })
      }
    }
    return codeRegistry.build()
  }

  @Bean
  fun scalars(coercingList: List<Coercing<*, *>>): List<GraphQLScalarType> {
    return coercingList.mapNotNull {
      val graphqlScalar = it::class.findAnnotation<GraphqlScalar>()

      if (graphqlScalar === null) {
        logger.debug(
          "Bean '%s' does not have annotation '%s'.", it.javaClass.canonicalName, GraphqlScalar::class.qualifiedName
        )
        return@mapNotNull null
      }

      val name = graphqlScalar.name

      if (name.isBlank()) {
        logger.error("Bean '%s' should has a name, but be blank.", it::class.qualifiedName)
        return@mapNotNull null
      }

      GraphQLScalarType.newScalar().name(name).coercing(it).build()
    }

  }

  @Bean
  fun graphql(
    codeRegistry: GraphQLCodeRegistry, scalars: List<GraphQLScalarType>, instrumentations: List<Instrumentation>
  ): GraphQL {
    val schemaParser = SchemaParser()

    val typeDefinitionRegistry = getSchemaFiles().map {
      InputStreamReader(it.inputStream, StandardCharsets.UTF_8).use { stream -> schemaParser.parse(stream) }
    }.fold(TypeDefinitionRegistry()) { a, b ->
      a.merge(b)
    }

    val schemaGenerator = SchemaGenerator()

    val runtimeWiring = RuntimeWiring.newRuntimeWiring().codeRegistry(codeRegistry)

    scalars.forEach { runtimeWiring.scalar(it) }

    val chainedInstrumentation = ChainedInstrumentation(instrumentations)

    val schema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring.build())
    return GraphQL.newGraphQL(schema).instrumentation(chainedInstrumentation)
      .subscriptionExecutionStrategy(SubscriptionExecutionStrategy()).build()
  }

  @Bean
  fun graphqlExecutionProcessor(
    graphql: GraphQL,
    batchLoaderMap: Map<String, BatchLoader<*, *>>,
    mappedBatchLoaderMap: Map<String, MappedBatchLoader<*, *>>,
  ) = GraphqlExecutionProcessor(graphql, batchLoaderMap, mappedBatchLoaderMap)

}
