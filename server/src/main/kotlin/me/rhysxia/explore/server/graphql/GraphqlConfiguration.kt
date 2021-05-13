package me.rhysxia.explore.server.graphql

import graphql.GraphQL
import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GraphqlConfiguration {

  @Bean
  fun graphql(): GraphQL {
    val o = GraphQLObjectType.newObject()
      .name("user")
      .field(
        GraphQLFieldDefinition.newFieldDefinition()
          .name("username")
          .type(Scalars.GraphQLString)
      )
      .build()

    val schema = GraphQLSchema
      .newSchema()
      .query(o)
//      .additionalType(o)
      .build()

    return GraphQL.newGraphQL(schema).build()
  }
}