package me.rhysxia.explore.server.configuration.graphql

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.support.DefaultHandshakeHandler

@Configuration
@EnableWebSocket
class WebSocketConfiguration(private val graphqlWebsocket: GraphqlWebsocket) : WebSocketConfigurer {
  override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
    val defaultHandshakeHandler = DefaultHandshakeHandler()
    defaultHandshakeHandler.setSupportedProtocols("graphql-ws")
    registry
      .addHandler(graphqlWebsocket, "/subscriptions")
      .setHandshakeHandler(defaultHandshakeHandler)
      .setAllowedOrigins("*")
  }
}