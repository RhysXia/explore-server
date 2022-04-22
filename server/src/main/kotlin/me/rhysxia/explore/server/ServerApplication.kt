package me.rhysxia.explore.server

import graphql.analysis.MaxQueryComplexityInstrumentation
import graphql.execution.instrumentation.tracing.TracingInstrumentation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ServerApplication {

    @Bean
    fun tracingInstrumentation() = TracingInstrumentation()

    @Bean
    fun maxQueryComplexityInstrumentation() = MaxQueryComplexityInstrumentation(100)

}

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}
