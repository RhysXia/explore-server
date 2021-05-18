package me.rhysxia.explore.server.configuration.graphql

import graphql.ExecutionInput
import graphql.GraphQL
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
class GraphqlController(private val graphql: GraphQL, private val loaderMap: Map<String, BatchLoader<*, *>>) {

    private val logger = LoggerFactory.getLogger(this.javaClass)


    @PostMapping("/graphql")
    fun graphql(@RequestBody graphqlRequestBody: GraphqlRequestBody): Mono<MutableMap<String, Any>> {
        val dataLoaderRegister = DataLoaderRegistry()

        this.loaderMap.forEach {
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