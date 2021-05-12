package me.rhysxia.explore.server

import graphql.GraphQL
import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition
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
            .field(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("username")
                    .type(Scalars.GraphQLString)
            )
            .build()

        val schema = GraphQLSchema
            .newSchema()
            .additionalType(o)
            .build()

        return GraphQL.newGraphQL(schema).build()
    }
}

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}
