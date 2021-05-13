package me.rhysxia.explore.server.controller

import graphql.ExecutionInput
import graphql.GraphQL
import me.rhysxia.explore.server.graphql.GraphqlBatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

data class GraphqlRequestEntity(
  val query: String?,
  val variables: MutableMap<String, Any>,
  val operationName: String?
)

@RestController
class GraphqlController(
  private val graphql: GraphQL,
  private val graphqlBatchLoaders: MutableList<GraphqlBatchLoader<Any, Any>>
) {

  @PostMapping("/graphql")
  fun query(
    @RequestBody entity: GraphqlRequestEntity
  ): Mono<Any> {

    val registry = DataLoaderRegistry()

    graphqlBatchLoaders.forEach {
      registry.register(it.getName(), DataLoader.newDataLoader(it))
    }

    val input = ExecutionInput.newExecutionInput()
      .query(entity.query)
      .operationName(entity.operationName)
      .variables(entity.variables)
      .dataLoaderRegistry(registry)
      .build()

    val cf = graphql.executeAsync(input)
    return cf.toMono().map {
      if (it.errors.isNotEmpty()) {
        mapOf("error" to it.errors)
      } else {
        mapOf("data" to it.getData())
      }
    }
  }
}