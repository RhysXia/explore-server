package me.rhysxia.explore.server.graphql

import graphql.GraphQL
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import me.rhysxia.explore.server.graphql.annotation.GraphqlField
import me.rhysxia.explore.server.graphql.annotation.GraphqlQuery
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GraphqlConfiguration {

  private fun getType(clazz: Class<Any>) {

  }

  @Bean
  fun graphql(ctx: ApplicationContext): GraphQL {
    val beanMap = ctx.getBeansWithAnnotation(GraphqlQuery::class.java)

    val queryObjectType = GraphQLObjectType.newObject()

    beanMap.forEach {
      val queryName = it.key
      val queryBean = it.value
      val queryDescription = queryBean.javaClass.getDeclaredAnnotation(GraphqlQuery::class.java).description

      val filedObjectType = GraphQLFieldDefinition.newFieldDefinition()
        .name(queryName)
        .description(queryDescription)

      val declaredMethods = queryBean::class.java.declaredMethods

      declaredMethods.forEach { method->
        val graphqlField = method.getDeclaredAnnotation(GraphqlField::class.java)
        if(graphqlField === null){}

        val fieldName = graphqlField.name
        val fieldDescription = graphqlField.description

        val

      }

      queryObjectType.field(
        filedObjectType
      )
    }

    val schema = GraphQLSchema
      .newSchema()
      .query(queryObjectType)
//      .additionalType(o)
      .build()

    return GraphQL.newGraphQL(schema).build()
  }
}