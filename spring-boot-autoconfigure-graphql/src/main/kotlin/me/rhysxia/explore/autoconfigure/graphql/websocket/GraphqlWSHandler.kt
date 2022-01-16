package me.rhysxia.explore.autoconfigure.graphql.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
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
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.ConcurrentHashMap

class GraphqlWSHandler(
//  private val tokenService: TokenService
  private val objectMapper: ObjectMapper, private val graphqlExecutionProcessor: GraphqlExecutionProcessor
) : WebSocketHandler {

  companion object {
    private const val GQL_CONNECTION_INIT = "connection_init"
    private const val GQL_CONNECTION_ACK = "connection_ack"
    private const val GQL_START = "start"
    private const val GQL_STOP = "stop"
    private const val GQL_DATA = "data"
    private const val GQL_ERROR = "error"
    private const val GQL_COMPLETE = "complete"
    private const val GQL_CONNECTION_TERMINATE = "connection_terminate"
  }


  private val logger = LoggerFactory.getLogger(this.javaClass)

  private val subscriptions = ConcurrentHashMap<String, MutableMap<String, Subscription>>()


  override fun handle(session: WebSocketSession): Mono<Void> {

    val message = session.receive()
      .map { it.payloadAsText }
      .map { objectMapper.readValue<OperationMessage>(it) }
      .map { (type, payload, id) ->
        when (type) {
          GQL_CONNECTION_INIT -> {
            logger.info("Initialized connection for {}", session.id)
            objectMapper.writeValueAsString(
              OperationMessage(GQL_CONNECTION_ACK)
            )
          }
          GQL_START -> {
            val requestBody = objectMapper.convertValue<GraphqlRequestBody>(payload!!)
            graphqlExecutionProcessor.doExecute(requestBody).toMono().map { executionResult ->
              val publisher = executionResult.getData<Publisher<ExecutionResult>>()
              publisher.toFlux()
                .map {
                  OperationMessage(GQL_DATA, DataPayload(it.getData()), id)
                }
            }
          }
          else -> {
            objectMapper.writeValueAsString(
              OperationMessage("error")
            )
          }
        }
      }.map { session.textMessage(objectMapper.writeValueAsString(it)) }

    return session.send(message)

//    return session.receive().map { it.payloadAsText }.map { objectMapper.readValue(it, OperationMessage::class.java) }
//      .doOnNext { (type, payload, id) ->
//        when (type) {
//          GQL_CONNECTION_INIT -> {
//            logger.info("Initialized connection for {}", session.id)
//            session.send(
//              Flux.just(
//                session.textMessage(
//                  objectMapper.writeValueAsString(
//                    OperationMessage(
//                      GQL_CONNECTION_ACK
//                    )
//                  )
//                )
//              )
//            )
//          }
//          GQL_START -> {
//            val queryPayload = jacksonObjectMapper().convertValue(payload, GraphqlRequestBody::class.java)
//            handleSubscription(id!!, queryPayload, session)
//          }
//          GQL_STOP -> {
//            subscriptions[session.id]?.get(id)?.cancel()
//            subscriptions.remove(id)
//          }
//          GQL_CONNECTION_TERMINATE -> {
//            logger.info("Terminated session " + session.id)
//            session.close()
//          }
//          else -> session.send(
//            Flux.just(
//              session.textMessage(
//                objectMapper.writeValueAsString(OperationMessage("error"))
//              )
//            )
//          )
//        }
//      }.then()

  }

  override fun getSubProtocols(): List<String> {
    return listOf("graphql-ws")
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
        val message = OperationMessage(GQL_DATA, DataPayload(er.getData()), id)
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



