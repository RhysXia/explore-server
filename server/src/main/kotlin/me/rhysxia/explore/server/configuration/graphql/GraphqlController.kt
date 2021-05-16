package me.rhysxia.explore.server.configuration.graphql

import graphql.ExecutionInput
import graphql.GraphQL
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlLoader
import org.dataloader.BatchLoader
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
class GraphqlController {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  private val graphql: GraphQL
  private val loaders: Map<String, BatchLoader<Any, Any>>

  constructor(graphql: GraphQL, loaders: List<BatchLoader<Any, Any>>) {
    this.graphql = graphql
    loaders.mapNotNull {
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
    }.fold(HashMap<String, BatchLoader<Any, Any>>()) { a, b ->
      a.put(b.first, b.second)
    }

  }

  @PostMapping("/graphql")
  fun graphql(@RequestBody graphqlRequestBody: GraphqlRequestBody): Mono<MutableMap<String, Any>> {
    val executionInput = ExecutionInput.newExecutionInput()
      .query(graphqlRequestBody.query)
      .variables(graphqlRequestBody.variables)
      .operationName(graphqlRequestBody.operationName)
      .build()

    return graphql.executeAsync(executionInput).toMono().map { it.toSpecification() }

  }
}