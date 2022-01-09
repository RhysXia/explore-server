package me.rhysxia.explore.autoconfigure.graphql

import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import org.dataloader.BatchLoader
import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderRegistry
import org.dataloader.MappedBatchLoader
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class GraphqlExecutionProcessor(
  private val graphql: GraphQL,
  private val batchLoaderMap: Map<String, BatchLoader<*, *>>,
  private val mappedBatchLoaderMap: Map<String, MappedBatchLoader<*, *>>,
) {
  fun doExecute(graphqlRequestBody: GraphqlRequestBody): Mono<ExecutionResult> {
    val dataLoaderRegister = DataLoaderRegistry()

    batchLoaderMap.forEach { (key, value) ->
      dataLoaderRegister.register(key, DataLoaderFactory.newDataLoader(value))
    }

    mappedBatchLoaderMap.forEach { (key, value) ->
      dataLoaderRegister.register(key, DataLoaderFactory.newMappedDataLoader(value))
    }

    val executionInput =
      ExecutionInput.newExecutionInput().query(graphqlRequestBody.query).variables(graphqlRequestBody.variables)
        .operationName(graphqlRequestBody.operationName).extensions(graphqlRequestBody.extensions)
        .dataLoaderRegistry(dataLoaderRegister).build()

    return graphql.executeAsync(executionInput).toMono()
  }

}