package me.rhysxia.explore.server.graphql

import graphql.GraphQL
import graphql.schema.*
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import me.rhysxia.explore.server.graphql.annotation.GraphqlData
import me.rhysxia.explore.server.graphql.annotation.GraphqlFetcher
import me.rhysxia.explore.server.graphql.annotation.GraphqlInput
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets


@Configuration
class GraphqlConfiguration {

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


    fun graphQLCodeRegistry(ctx: ApplicationContext): GraphQLCodeRegistry {
        val codeRegistry = GraphQLCodeRegistry.newCodeRegistry()
        ctx.getBeansWithAnnotation(GraphqlFetcher::class.java).forEach{
            val bean = it.value
            bean::class.java.methods.forEach { method ->
                val graphqlData = method.getDeclaredAnnotation(GraphqlData::class.java)
                if(graphqlData != null) {
                    val parentType = graphqlData.parentType
                    val fieldName = graphqlData.fieldName
                    val mappings = method.parameters.map { parameter ->
                        if(parameter.type.isAssignableFrom(DataFetchingEnvironment::class.java)) {
                            null
                        }
                        val graphqlInput = parameter.getDeclaredAnnotation(GraphqlInput::class.java)
                        if(graphqlInput === null) parameter.name else graphqlInput.name
                    }
                    codeRegistry.dataFetcher(FieldCoordinates.coordinates(parentType,fieldName), DataFetcher {
                        val args = mappings
                        method.invoke(bean,)
                    })
                }
            }
        }

        return codeRegistry.build()
    }

    @Bean
    fun graphql(): GraphQL {
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

        val runtimeWiring =RuntimeWiring.newRuntimeWiring()
            .type()

        schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)

    }
}