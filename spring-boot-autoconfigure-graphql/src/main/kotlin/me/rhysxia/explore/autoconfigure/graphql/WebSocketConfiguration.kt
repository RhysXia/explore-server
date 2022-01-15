package me.rhysxia.explore.autoconfigure.graphql

import me.rhysxia.explore.autoconfigure.graphql.websocket.GraphqlTWSHandler
import me.rhysxia.explore.autoconfigure.graphql.websocket.GraphqlWSHandler
import me.rhysxia.explore.autoconfigure.graphql.websocket.GraphqlWebsocketDispatcherHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.support.DefaultHandshakeHandler

@Configuration
@EnableWebSocket
@ConditionalOnWebApplication
class WebSocketConfiguration(
  private val graphqlExecutionProcessor: GraphqlExecutionProcessor,
//  private val tokenService: TokenService,
  private val graphqlConfigurator: GraphqlConfigurationProperties
) : WebSocketConfigurer {

  override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {

    val handlerMap = mapOf(
      "graphql-ws" to GraphqlWSHandler(graphqlExecutionProcessor),
      "graphql-transport-ws" to GraphqlTWSHandler(graphqlExecutionProcessor)
    )

    val defaultHandshakeHandler = DefaultHandshakeHandler()
    defaultHandshakeHandler.setSupportedProtocols("graphql-ws", "graphql-transport-ws")
    registry.addHandler(GraphqlWebsocketDispatcherHandler(handlerMap), graphqlConfigurator.subscription.endpoint)
      .setHandshakeHandler(defaultHandshakeHandler)
//      .addInterceptors(AuthHandshakeInterceptor(tokenService))
      .setAllowedOrigins("*")

  }
}