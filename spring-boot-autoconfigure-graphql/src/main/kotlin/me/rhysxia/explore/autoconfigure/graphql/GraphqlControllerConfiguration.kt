package me.rhysxia.explore.autoconfigure.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
import reactor.kotlin.core.publisher.toMono

@Configuration
class GraphqlControllerConfiguration {

  @Bean
  fun schemaRouter(
    graphqlConfigurationProperties: GraphqlConfigurationProperties,
    graphqlExecutionProcessor: GraphqlExecutionProcessor,
    objectMapper: ObjectMapper
  ) = coRouter {
    (graphqlConfigurationProperties.query.endpoint and accept(
      MediaType.APPLICATION_JSON, MediaType.valueOf("application/graphql")
    )).nest {
      POST("") {
        val req = it.awaitBody<GraphqlRequestBody>()

        val er = graphqlExecutionProcessor.doExecute(req) { builder ->
          builder.fromServerRequest(it)
        }.toMono().awaitSingle()
        val result = er.toSpecification()
        ok().bodyValueAndAwait(result)
      }
      GET("") {
        val variablesString = it.queryParam("variables").orElse("{}")
        val extensionsString = it.queryParam("extensions").orElse("{}")
        val operationName = it.queryParam("operationName").orElse(null)
        val query = it.queryParam("query").orElse("")

        val variables = objectMapper.readValue<Map<String, Any>>(variablesString)
        val extensions = objectMapper.readValue<Map<String, Any>>(extensionsString)

        val er = graphqlExecutionProcessor.doExecute(
          GraphqlRequestBody(
            variables, extensions, operationName, query
          )
        ) { builder ->
          builder.fromServerRequest(it)
        }.toMono().awaitSingle()
        val result = er.toSpecification()
        ok().bodyValueAndAwait(result)
      }
    }
  }


}

