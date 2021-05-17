package me.rhysxia.explore.server.configuration.graphql

import graphql.ExecutionInput
import graphql.GraphQL
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlLoader
import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

data class GraphqlRequestBody(
    val query: String?,
    val variables: Map<String, Any> = emptyMap(),
    val operationName: String?
)

@RestController
class GraphqlController(private val graphql: GraphQL, loaders: List<BatchLoader<*, *>>) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val loaders: Map<String, BatchLoader<*, *>>

    init {
        this.loaders = loaders.mapNotNull {
            val graphqlLoader = it.javaClass.getAnnotation(GraphqlLoader::class.java)
            if (graphqlLoader === null) {
                logger.debug(
                    "BatchLoader '%s' should has annotation '%s'.",
                    it.javaClass.canonicalName,
                    GraphqlLoader::class.java
                )
                return@mapNotNull null
            }
            val name = graphqlLoader.name
            if (name.isBlank()) {
                logger.debug(
                    "BatchLoader '%s' should has a name, but be blank.",
                    it.javaClass.canonicalName
                )
                return@mapNotNull null
            }
            name to it
        }.fold(HashMap<String, BatchLoader<*, *>>()) { a, b ->
            a[b.first] = b.second
            a
        }
    }

    @PostMapping("/graphql")
    fun graphql(@RequestBody graphqlRequestBody: GraphqlRequestBody): Mono<MutableMap<String, Any>> {
        val dataLoaderRegister = DataLoaderRegistry()

        this.loaders.forEach {
            dataLoaderRegister.register(it.key, DataLoader.newDataLoader(it.value))
        }

        val executionInput = ExecutionInput.newExecutionInput()
            .query(graphqlRequestBody.query)
            .variables(graphqlRequestBody.variables)
            .operationName(graphqlRequestBody.operationName)
            .dataLoaderRegistry(dataLoaderRegister)
            .build()

        return graphql.executeAsync(executionInput).toMono().map { it.toSpecification() }

    }
}