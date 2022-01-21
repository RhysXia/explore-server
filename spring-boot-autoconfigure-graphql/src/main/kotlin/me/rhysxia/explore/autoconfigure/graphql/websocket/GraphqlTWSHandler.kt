package me.rhysxia.explore.autoconfigure.graphql.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import me.rhysxia.explore.autoconfigure.graphql.GraphqlConfigurationProperties
import me.rhysxia.explore.autoconfigure.graphql.GraphqlExecutionProcessor
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

class GraphqlTWSHandler(
  private val objectMapper: ObjectMapper,
  private val graphqlExecutionProcessor: GraphqlExecutionProcessor,
  private val graphqlConfigurationProperties: GraphqlConfigurationProperties
) : WebSocketHandler {

  private val graphqlTWSEnhancer = GraphqlWebsocketEnhancer.Builder()
    .before("connection_init", "connection_ack")
    .run("subscribe", "next")
    .errorRespondCode("error")
    .stopReceiveCode("stop")
    .completeRespondCode("complete")
    .build()

  override fun handle(session: WebSocketSession): Mono<Void> {
    return graphqlTWSEnhancer.execute(session, objectMapper, graphqlExecutionProcessor, graphqlConfigurationProperties)
  }

  override fun getSubProtocols(): List<String> {
    return listOf("graphql-transport-ws")
  }

}


