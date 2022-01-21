package me.rhysxia.explore.autoconfigure.graphql.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import me.rhysxia.explore.autoconfigure.graphql.GraphqlConfigurationProperties
import me.rhysxia.explore.autoconfigure.graphql.GraphqlExecutionProcessor
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

class GraphqlWSHandler(
  private val objectMapper: ObjectMapper,
  private val graphqlExecutionProcessor: GraphqlExecutionProcessor,
  private val graphqlConfigurationProperties: GraphqlConfigurationProperties
) : WebSocketHandler {


  private val graphqlWSEnhancer = GraphqlWebsocketEnhancer.Builder()
    .before("connection_init", "connection_ack")
    .run("start", "data")
    .errorRespondCode("error")
    .stopReceiveCode("stop")
    .completeRespondCode("complete")
    .terminateReceiveCode("connection_terminate")
    .build()

  override fun handle(session: WebSocketSession): Mono<Void> {
    return graphqlWSEnhancer.execute(session, objectMapper, graphqlExecutionProcessor, graphqlConfigurationProperties)
  }

  override fun getSubProtocols(): List<String> {
    return listOf("graphql-ws")
  }
}



