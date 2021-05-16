package me.rhysxia.explore.server.graphql

import graphql.ExecutionInput
import graphql.GraphQL
import graphql.language.InputObjectTypeDefinition
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

data class GraphqlRequestBody(val input: String?,val variables: Map<String, Any> = emptyMap(), val operationName: String?)

@RestController
class GraphqlController(private val graphql:GraphQL) {

    @PostMapping("/graphql")
    fun graphql(graphqlRequestBody: GraphqlRequestBody) {
       val executionInput = ExecutionInput.newExecutionInput()
            .query(graphqlRequestBody.input)
            .variables(graphqlRequestBody.variables)
            .operationName(graphqlRequestBody.operationName)
            .build()


        Schema

    }
}