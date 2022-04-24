package me.rhysxia.explore.server.configuration

import graphql.analysis.MaxQueryComplexityInstrumentation
import graphql.execution.instrumentation.tracing.TracingInstrumentation
import graphql.scalars.ExtendedScalars
import graphql.schema.GraphQLScalarType
import me.rhysxia.explore.server.graphql.coercing.TimestampCoercing
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GraphqlConfiguration {

    @Bean
    fun tracingInstrumentation() = TracingInstrumentation()

    @Bean
    fun maxQueryComplexityInstrumentation() = MaxQueryComplexityInstrumentation(100)

    @Bean
    fun longScalar() = ExtendedScalars.GraphQLLong

    @Bean
    fun timestampScalar() = GraphQLScalarType.newScalar().name("Timestamp").coercing(TimestampCoercing()).build()
}