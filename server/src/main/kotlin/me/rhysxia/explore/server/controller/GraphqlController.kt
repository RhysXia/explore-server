package me.rhysxia.explore.server.controller

import graphql.ExecutionInput
import graphql.GraphQL
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class QueryData(val query: String?, val operationName: String?, val variables: MutableMap<String, Any>)

@RestController
@RequestMapping("/graphql")
class GraphqlController(private val graphQL: GraphQL) {

  @PostMapping
  fun query(
    @RequestBody(required = false) body: QueryData,
  ): Any {
    val input =
      ExecutionInput.newExecutionInput(body.query).variables(body.variables).operationName(body.operationName)
        .build()
    val result = graphQL.execute(input)
    return result
  }
}