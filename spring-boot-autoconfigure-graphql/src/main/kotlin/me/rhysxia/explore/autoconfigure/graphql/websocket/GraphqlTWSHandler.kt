package me.rhysxia.explore.autoconfigure.graphql.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.ExecutionResult
import me.rhysxia.explore.autoconfigure.graphql.GraphqlExecutionProcessor
import me.rhysxia.explore.autoconfigure.graphql.GraphqlRequestBody
import org.reactivestreams.Publisher
import org.reactivestreams.Subscription
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.util.concurrent.ConcurrentHashMap

class GraphqlTWSHandler(
  private val objectMapper: ObjectMapper, private val graphqlExecutionProcessor: GraphqlExecutionProcessor
//  private val tokenService: TokenService
) : WebSocketHandler {

  companion object {
    private const val GQL_CONNECTION_INIT = "connection_init"
    private const val GQL_CONNECTION_ACK = "connection_ack"
    private const val GQL_SUBSCRIBE = "subscribe"
    private const val GQL_STOP = "stop"
    private const val GQL_NEXT = "next"
    private const val GQL_ERROR = "error"
    private const val GQL_COMPLETE = "complete"
  }


  private val logger = LoggerFactory.getLogger(this.javaClass)

  private val subscriptions = ConcurrentHashMap<String, MutableMap<String, Subscription>>()

  override fun handle(session: WebSocketSession): Mono<Void> {
    return session.receive().map { it.payloadAsText }.map { objectMapper.readValue(it, OperationMessage::class.java) }
      .doOnNext { (type, payload, id) ->
        when (type) {
          GQL_CONNECTION_INIT -> {
            logger.info("Initialized connection for {}", session.id)
            session.send(
              Flux.just(
                session.textMessage(
                  objectMapper.writeValueAsString(
                    OperationMessage(
                      GQL_CONNECTION_ACK
                    )
                  )
                )
              )
            )
          }
          GQL_SUBSCRIBE -> {
            val requestBody = objectMapper.convertValue(payload, GraphqlRequestBody::class.java)
            handleSubscription(id!!, requestBody, session)
          }
          GQL_STOP -> {
            subscriptions[session.id]?.get(id)?.cancel()
            subscriptions.remove(id)
          }
          GQL_COMPLETE -> {
            logger.info("Terminated session " + session.id)
            session.close()
          }
          else -> session.send(
            Flux.just(
              session.textMessage(
                objectMapper.writeValueAsString(OperationMessage("error"))
              )
            )
          )
        }
      }.then()
  }

  override fun getSubProtocols(): List<String> {
    return listOf("graphql-transport-ws")
  }


  private fun handleSubscription(id: String, graphqlRequestBody: GraphqlRequestBody, session: WebSocketSession) {

    graphqlExecutionProcessor.doExecute(graphqlRequestBody).thenAccept {
      val publisher = it.getData<Publisher<ExecutionResult>>()
      publisher.toFlux().doOnSubscribe { s ->
        var map = subscriptions[session.id]
        if (map === null) {
          map = mutableMapOf(Pair(id, s))
          subscriptions[session.id] = map
        } else {
          map[id] = s
        }
        s.request(1)
      }.doOnNext { er ->
        val message = OperationMessage(GQL_NEXT, DataPayload(er.getData()), id)
        val jsonMessage = session.textMessage(objectMapper.writeValueAsString(message))
        logger.debug("Sending subscription data: {}", jsonMessage)

        if (session.isOpen) {
          session.send(Flux.just(jsonMessage)).doOnNext {
            subscriptions[session.id]?.get(id)?.request(1)
          }
        }
      }.doOnError { t ->
        logger.error("Error on subscription {}", id, t)
        val message = OperationMessage(GQL_ERROR, DataPayload(null, listOf(t.message!!)), id)
        val jsonMessage = session.textMessage((objectMapper.writeValueAsString(message)))
        logger.debug("Sending subscription error: {}", jsonMessage)

        if (session.isOpen) {
          session.send(Flux.just(jsonMessage))
        }
        subscriptions.remove(id)
      }.doOnComplete {
        logger.info("Subscription completed for {}", id)
        val message = OperationMessage(GQL_COMPLETE, null, id)
        val jsonMessage = session.textMessage(objectMapper.writeValueAsString(message))

        if (session.isOpen) {
          session.send(Flux.just(jsonMessage))
        }

        subscriptions.remove(id)
      }.doOnCancel {
        subscriptions.remove(id)
      }


    }
  }
}


