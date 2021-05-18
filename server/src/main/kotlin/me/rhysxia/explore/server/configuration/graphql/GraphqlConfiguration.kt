package me.rhysxia.explore.server.configuration.graphql

import graphql.GraphQL
import graphql.schema.*
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import me.rhysxia.explore.server.configuration.graphql.annotation.*
import me.rhysxia.explore.server.dto.OffsetPage
import org.dataloader.BatchLoader
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.Continuation


@Configuration
class GraphqlConfiguration {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  private val schemaLocation = "classpath*:/graphql/**/*.graphql*"

  fun getSchemaFiles(): Array<Resource> {
    val loader = Thread.currentThread().contextClassLoader

    val pathMatchingResourcePatternResolver = PathMatchingResourcePatternResolver(loader)
    return try {
      pathMatchingResourcePatternResolver.getResources(this.schemaLocation)
    } catch (e: Exception) {
      emptyArray()
    }
  }

  @Bean
  fun <ID, Entity> batchLoaderMap(
    batchLoaders: List<BatchLoader<ID, Entity>>,
    graphqlBatchLoaders: List<GraphqlBatchLoader<ID, Entity>>,
  ): MutableMap<String, BatchLoader<ID, Entity>> {
    val pair1 = graphqlBatchLoaders.mapNotNull { graphqlBatchLoader ->
      val graphqlLoader = graphqlBatchLoader.javaClass.getAnnotation(GraphqlLoader::class.java)
      if (graphqlLoader === null) {
        return@mapNotNull null
      }
      val name = graphqlLoader.name
      if (name.isBlank()) {
        logger.debug(
          "BatchLoader '%s' should has a name, but be blank.",
          graphqlBatchLoader.javaClass.canonicalName
        )
        return@mapNotNull null
      }
      val loader = BatchLoader<ID, Entity> {
        val future = CompletableFuture<List<Entity?>>()
        GlobalScope.launch {
          val list = graphqlBatchLoader.load(it).toList()
          future.complete(list)
        }

        future
      }
      name to loader
    }

    val pair2 = batchLoaders.mapNotNull { batchLoader ->
      val graphqlLoader = batchLoader.javaClass.getAnnotation(GraphqlLoader::class.java)
      if (graphqlLoader === null) {
        return@mapNotNull null
      }
      val name = graphqlLoader.name
      if (name.isBlank()) {
        logger.debug(
          "BatchLoader '%s' should has a name, but be blank.",
          batchLoader.javaClass.canonicalName
        )
        return@mapNotNull null
      }
      name to batchLoader
    }

    return (pair1 + pair2).fold(HashMap<String, BatchLoader<ID, Entity>>()) { a, b ->
      a[b.first] = b.second
      a
    }
  }

  @Bean
  fun graphQLCodeRegistry(ctx: ApplicationContext): GraphQLCodeRegistry {
    val codeRegistry = GraphQLCodeRegistry.newCodeRegistry()
    ctx.getBeansWithAnnotation(GraphqlData::class.java).forEach {
      val bean = it.value
      val rootParentType = bean.javaClass.getAnnotation(GraphqlData::class.java).parentType
      bean::class.java.methods.forEach beanForEach@{ method ->
        val graphqlFetcher = method.getDeclaredAnnotation(GraphqlFetcher::class.java)
        if (graphqlFetcher === null) {
          return@beanForEach
        }
        val parentType = graphqlFetcher.parentType.ifBlank { rootParentType }
        val fieldName = graphqlFetcher.fieldName.ifBlank { method.name }
        if (parentType.isBlank()) {
          logger.error(
            "GraphqlFetcher '%s' should have a parentType, but be blank.",
            method.javaClass.canonicalName
          )
        }
        val last = method.parameters.lastOrNull()
        val isSuspend = if (last !== null) last.type.isAssignableFrom(Continuation::class.java) else false

        val isFlow = method.returnType.isAssignableFrom(Flow::class.java)

        codeRegistry.dataFetcher(FieldCoordinates.coordinates(parentType, fieldName), DataFetcher {
          val parameters = if (isSuspend) method.parameters.sliceArray(IntRange(0, -1)) else method.parameters
          val args = parameters.map { parameter ->
            val type = parameter.type
            if (type.isAssignableFrom(DataFetchingEnvironment::class.java)) {
              return@map it
            }
            if (type.isAssignableFrom(Pageable::class.java)) {
              val offset = it.getArgument<Long>("offset")
              val limit = it.getArgument<Int>("limit")
              val sort = it.getArgument<Sort>("sort")
              return@map OffsetPage(offset, limit, sort)
            }

            val graphqlInput = parameter.getDeclaredAnnotation(GraphqlInput::class.java)
            val name = if (graphqlInput === null) parameter.name else graphqlInput.name
            it.getArgument(name)
          }


          if (isSuspend) {
            val future = CompletableFuture<Any>()
            GlobalScope.launch {
              val result = method.invoke(bean, *args.toTypedArray(), this)
              future.complete(result)
            }
            return@DataFetcher future
          }

          val result = method.invoke(bean, *args.toTypedArray()) as Flow<*>

          if (isFlow) {
            val future = CompletableFuture<List<*>>()
            GlobalScope.launch {
              val list = result.toList()
              future.complete(list)
            }
            return@DataFetcher future
          }
          return@DataFetcher result
        })
      }
    }

    return codeRegistry.build()
  }

  @Bean
  fun scalars(coercingList: List<Coercing<*, *>>): List<GraphQLScalarType> {
    return coercingList.mapNotNull {
      val graphqlScalar = it::class.java.getAnnotation(GraphqlScalar::class.java)

      if (graphqlScalar === null) {
        logger.debug(
          "Bean '%s' does not have annotation '%s'.",
          it.javaClass.canonicalName,
          GraphqlScalar::class.java.canonicalName
        )
        return@mapNotNull null
      }

      val name = graphqlScalar.name

      if (name.isBlank()) {
        logger.error("Bean '%s' should has a name, but be blank.", it.javaClass.canonicalName)
        return@mapNotNull null
      }

      GraphQLScalarType.newScalar()
        .name(name)
        .coercing(it)
        .build()
    }

  }

  @Bean
  fun graphql(codeRegistry: GraphQLCodeRegistry, scalars: List<GraphQLScalarType>): GraphQL {
    val schemaParser = SchemaParser()

    val typeDefinitionRegistry = getSchemaFiles()
      .map {
        InputStreamReader(it.inputStream, StandardCharsets.UTF_8)
          .use { stream -> schemaParser.parse(stream) }
      }
      .fold(TypeDefinitionRegistry()) { a, b ->
        a.merge(b)
      }

    val schemaGenerator = SchemaGenerator()

    val runtimeWiring = RuntimeWiring.newRuntimeWiring()
      .codeRegistry(codeRegistry)

    scalars.forEach { runtimeWiring.scalar(it) }


    val schema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring.build())
    return GraphQL.newGraphQL(schema).build()
  }
}
