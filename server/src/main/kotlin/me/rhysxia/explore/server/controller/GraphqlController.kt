package me.rhysxia.explore.server.controller

import graphql.ExecutionInput
import graphql.schema.GraphQLInputType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class GraphqlRequestEntity(
  val query: String?,
  val variables: MutableMap<String, Any>,
  val operationName: String?
)

@RestController
class GraphqlController {

  @PostMapping("/graphql")
  fun query(
    @RequestBody entity: GraphqlRequestEntity
  ) {
    val input = ExecutionInput.newExecutionInput()
      .query(entity.query)
      .operationName(entity.operationName)
      .variables(entity.variables)
      .build()
  }
}