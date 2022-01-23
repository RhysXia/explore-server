package me.rhysxia.explore.autoconfigure.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import me.rhysxia.explore.autoconfigure.graphql.websocket.GraphqlTWSHandler
import me.rhysxia.explore.autoconfigure.graphql.websocket.GraphqlWSHandler
import me.rhysxia.explore.autoconfigure.graphql.websocket.GraphqlWebsocketDispatcherHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler


@Configuration
class GraphqlWebSocketConfiguration {

  @Bean
  fun webSocketHandlerMapping(
    objectMapper: ObjectMapper,
    graphqlExecutionProcessor: GraphqlExecutionProcessor,
    graphqlConfigurationProperties: GraphqlConfigurationProperties
  ): HandlerMapping {
    val map: Map<String, WebSocketHandler> = mapOf(
      graphqlConfigurationProperties.subscription.endpoint to GraphqlWebsocketDispatcherHandler(
        listOf(
          GraphqlTWSHandler(objectMapper, graphqlExecutionProcessor),
          GraphqlWSHandler(objectMapper, graphqlExecutionProcessor)
        )
      )
    )

    val handlerMapping = SimpleUrlHandlerMapping()
    handlerMapping.order = 1
    handlerMapping.urlMap = map
//
//    val corsConfiguration = CorsConfiguration()
//    corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL)

//    handlerMapping.setCorsConfigurations(mapOf(graphqlConfigurationProperties.query.endpoint to corsConfiguration))

    return handlerMapping
  }


}