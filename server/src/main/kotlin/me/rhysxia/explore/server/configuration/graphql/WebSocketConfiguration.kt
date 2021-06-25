package me.rhysxia.explore.server.configuration.graphql

import me.rhysxia.explore.server.configuration.graphql.websocket.GraphqlTWSHandler
import me.rhysxia.explore.server.configuration.graphql.websocket.GraphqlWSHandler
import me.rhysxia.explore.server.service.TokenService
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.support.DefaultHandshakeHandler

@Configuration
@EnableWebSocket
class WebSocketConfiguration(
  private val graphqlWSHandler: GraphqlWSHandler,
  private val graphqlTWSHandler: GraphqlTWSHandler,
  private val tokenService: TokenService
) : WebSocketConfigurer {
  override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
    val defaultHandshakeHandler = DefaultHandshakeHandler()
    defaultHandshakeHandler.setSupportedProtocols("graphql-ws")
    registry
      .addHandler(graphqlWSHandler, "/subscriptions")
      .setHandshakeHandler(defaultHandshakeHandler)
      .addInterceptors(AuthHandshakeInterceptor(tokenService))
      .setAllowedOrigins("*")

    val defaultHandshakeHandler2 = DefaultHandshakeHandler()
    defaultHandshakeHandler2.setSupportedProtocols("graphql-transport-ws")
    registry
      .addHandler(graphqlTWSHandler, "/subscriptions")
      .setHandshakeHandler(defaultHandshakeHandler2)
      .addInterceptors(AuthHandshakeInterceptor(tokenService))
      .setAllowedOrigins("*")
  }
}