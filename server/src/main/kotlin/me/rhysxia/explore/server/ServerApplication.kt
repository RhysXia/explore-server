package me.rhysxia.explore.server

import graphql.GraphQL
import graphql.Scalars.GraphQLString
import graphql.language.FieldDefinition.newFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLSchema
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class ServerApplication {
  @Bean
  fun graphQL(): GraphQL {
    val o = newObject()
      .name("user")
      .field(newFieldDefinition()
          .name("username")
          .type(GraphQLString))
      .build()



    return GraphQL.newGraphQL(schema).build()
  }
}

fun main(args: Array<String>) {
  runApplication<ServerApplication>(*args)
}
