package me.rhysxia.explore.server.configuration.graphql.controller

import graphql.ExecutionInput
import graphql.GraphQL
import me.rhysxia.explore.server.configuration.graphql.AuthFilter
import me.rhysxia.explore.server.configuration.graphql.GraphqlConfigurator
import me.rhysxia.explore.server.dto.AuthUser
import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.dataloader.MappedBatchLoader
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono


@RestController
class GraphqlController(
  private val graphql: GraphQL,
  private val batchLoaderMap: Map<String, BatchLoader<*, *>>,
  private val mappedBatchLoaderMap: Map<String, MappedBatchLoader<*, *>>,
  private val graphqlConfigurator: GraphqlConfigurator
) {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  @CrossOrigin
  @PostMapping("/graphql")
  fun graphql(
    @RequestBody graphqlRequestBody: GraphqlRequestBody,
    @RequestAttribute(AuthFilter.USER_KEY, required = false) authUser: AuthUser?
  ): Mono<MutableMap<String, Any>> {
    val dataLoaderRegister = DataLoaderRegistry()

    this.batchLoaderMap.forEach { (key, value) ->
      dataLoaderRegister.register(key, DataLoader.newDataLoader(value))
    }

    this.mappedBatchLoaderMap.forEach { (key, value) ->
      dataLoaderRegister.register(key, DataLoader.newMappedDataLoader(value))
    }

    val executionInput = ExecutionInput.newExecutionInput()
      .context {
        if (authUser != null) it.of(AuthFilter.USER_KEY, authUser) else it
      }
      .query(graphqlRequestBody.query)
      .variables(graphqlRequestBody.variables)
      .operationName(graphqlRequestBody.operationName)
      .extensions(graphqlRequestBody.extensions)
      .dataLoaderRegistry(dataLoaderRegister)
      .build()

    return graphql.executeAsync(executionInput).toMono().map { it.toSpecification() }
  }
}