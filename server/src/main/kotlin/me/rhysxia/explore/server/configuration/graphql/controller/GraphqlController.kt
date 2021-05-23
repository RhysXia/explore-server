package me.rhysxia.explore.server.configuration.graphql.controller

import graphql.ExecutionInput
import graphql.GraphQL
import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.dataloader.MappedBatchLoader
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono


@RestController
class GraphqlController(
  private val graphql: GraphQL,
  private val batchLoaderMap: Map<String, BatchLoader<*, *>>,
  private val mappedBatchLoaderMap: Map<String, MappedBatchLoader<*, *>>
) {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  @CrossOrigin
  @PostMapping("/graphql")
  fun graphql(@RequestBody graphqlRequestBody: GraphqlRequestBody): Mono<MutableMap<String, Any>> {
    val dataLoaderRegister = DataLoaderRegistry()

    this.batchLoaderMap.forEach { (key, value) ->
      dataLoaderRegister.register(key, DataLoader.newDataLoader(value))
    }

    this.mappedBatchLoaderMap.forEach { (key, value) ->
      dataLoaderRegister.register(key, DataLoader.newMappedDataLoader(value))
    }

    val executionInput = ExecutionInput.newExecutionInput()
      .query(graphqlRequestBody.query)
      .variables(graphqlRequestBody.variables)
      .operationName(graphqlRequestBody.operationName)
      .extensions(graphqlRequestBody.extensions)
      .dataLoaderRegistry(dataLoaderRegister)
      .build()

    return graphql.executeAsync(executionInput).toMono().map { it.toSpecification() }
  }
}