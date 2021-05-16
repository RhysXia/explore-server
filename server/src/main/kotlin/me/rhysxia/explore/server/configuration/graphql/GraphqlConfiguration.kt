package me.rhysxia.explore.server.configuration.graphql

import graphql.GraphQL
import graphql.schema.*
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlData
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlFetcher
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlInput
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlScalar
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets


@Configuration
class GraphqlConfiguration {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  private val schemaLocation = "classpath*:/graphql/**/*.graphql*"

  fun getSchemaFiles(): Array<Resource> {
    val loader = Thread.currentThread().contextClassLoader

    val pathMatchingResourcePatternResolver = PathMatchingResourcePatternResolver(loader)
    val schema = try {
      pathMatchingResourcePatternResolver.getResources(this.schemaLocation)
    } catch (e: Exception) {
      emptyArray()
    }
    return schema
  }


  @Bean
  fun graphQLCodeRegistry(ctx: ApplicationContext): GraphQLCodeRegistry {
    val codeRegistry = GraphQLCodeRegistry.newCodeRegistry()
    ctx.getBeansWithAnnotation(GraphqlData::class.java).forEach {
      val bean = it.value
      val rootParentType = bean.javaClass.getAnnotation(GraphqlData::class.java).parentType
      bean::class.java.methods.forEach { method ->
        val graphqlFetcher = method.getDeclaredAnnotation(GraphqlFetcher::class.java)
        if (graphqlFetcher === null) {
          return@forEach
        }
        val parentType = graphqlFetcher.parentType.ifBlank { rootParentType }
        val fieldName = graphqlFetcher.fieldName.ifBlank { method.name }
        if (parentType.isBlank()) {
          logger.error("GraphqlFetcher '%s' should have a parentType, but be blank.", method.javaClass.canonicalName)
        }

        if (parentType.isBlank()) {
          logger.error("GraphqlFetcher '%s' should have a fieldName, but be blank.", method.javaClass.canonicalName)
        }

        val mappings = method.parameters.map { parameter ->
          if (parameter.type.isAssignableFrom(DataFetchingEnvironment::class.java)) {
            return@map null
          }
          val graphqlInput = parameter.getDeclaredAnnotation(GraphqlInput::class.java)
          if (graphqlInput === null) parameter.name else graphqlInput.name
        }
        codeRegistry.dataFetcher(FieldCoordinates.coordinates(parentType, fieldName), DataFetcher {
          val args = mappings.map { mappingName ->
            if (mappingName === null) {
              it
            }
            it.getArgument<Any>(mappingName)
          }
          method.invoke(bean, *args.toTypedArray())
        })
      }
    }

    return codeRegistry.build()
  }

  @Bean
  fun scalars(ctx: ApplicationContext): List<GraphQLScalarType> {
    return ctx.getBeansWithAnnotation(GraphqlScalar::class.java).values.mapNotNull {
      if (it !is Coercing<*, *>) {
        logger.debug("Bean '%s' is not implement '%s'.", it.javaClass.canonicalName, Coercing::class.java.canonicalName)
        return@mapNotNull null
      }
      val graphqlScalar = it::class.java.getAnnotation(GraphqlScalar::class.java)
      GraphQLScalarType.newScalar()
        .name(graphqlScalar.name)
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