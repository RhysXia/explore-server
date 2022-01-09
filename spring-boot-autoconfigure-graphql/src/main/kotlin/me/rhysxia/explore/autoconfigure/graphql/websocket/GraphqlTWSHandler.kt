package me.rhysxia.explore.autoconfigure.graphql.websocket

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.ExecutionResult
import me.rhysxia.explore.autoconfigure.graphql.GraphqlExecutionProcessor
import me.rhysxia.explore.autoconfigure.graphql.GraphqlRequestBody
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

@Component
class GraphqlTWSHandler(
  private val graphqlExecutionProcessor: GraphqlExecutionProcessor

//  private val tokenService: TokenService
) : TextWebSocketHandler() {

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

  internal val subscriptions = ConcurrentHashMap<String, MutableMap<String, Subscription>>()

  override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
    subscriptions[session.id]?.values?.forEach { it.cancel() }
    subscriptions.remove(session.id)
  }

  override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
    val (type, payload, id) = jacksonObjectMapper().readValue(message.payload, OperationMessage::class.java)
    when (type) {
      GQL_CONNECTION_INIT -> {
        logger.info("Initialized connection for {}", session.id)
//        checkAndSetUser(session, payload)
//        session.sendMessage(
//          TextMessage(
//            jacksonObjectMapper().writeValueAsBytes(
//              OperationMessage(
//                GQL_CONNECTION_ACK
//              )
//            )
//          )
//        )
      }
      GQL_SUBSCRIBE -> {
        val queryPayload = jacksonObjectMapper().convertValue(payload, GraphqlRequestBody::class.java)
        handleSubscription(id!!, queryPayload, session)
      }
      GQL_STOP -> {
        subscriptions[session.id]?.get(id)?.cancel()
        subscriptions.remove(id)
      }
      GQL_COMPLETE -> {
        logger.info("Terminated session " + session.id)
        session.close()
      }
      else -> session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsBytes(OperationMessage("error"))))
    }
  }

//  private fun checkAndSetUser(session: WebSocketSession, payload: Any?) {
//    if (session.attributes[me.rhysxia.explore.autoconfigure.graphql.AuthFilter.USER_KEY] != null) {
//      return
//    }
//    if (payload !is Map<*, *>) {
//      return
//    }
//
//    val token = payload["token"]
//
//    if (token !is String || token.isBlank()) {
//      return
//    }
//
//    runBlocking {
//      val authUser = tokenService.findAuthUserByToken(token)
//      if (authUser != null) {
//        session.attributes[me.rhysxia.explore.autoconfigure.graphql.AuthFilter.USER_KEY] = authUser
//      }
//    }
//  }

  private fun handleSubscription(id: String, graphqlRequestBody: GraphqlRequestBody, session: WebSocketSession) {

//    val authUser = session.attributes[me.rhysxia.explore.autoconfigure.graphql.AuthFilter.USER_KEY]

    graphqlExecutionProcessor.doExecute(graphqlRequestBody).doOnSuccess {
      val subscriptionStream: Publisher<ExecutionResult> = it.getData()
      subscriptionStream.subscribe(object : Subscriber<ExecutionResult> {
        override fun onSubscribe(s: Subscription) {
          logger.info("Subscription started for {}", id)
          subscriptions[session.id] = mutableMapOf(Pair(id, s))

          s.request(1)
        }

        override fun onNext(er: ExecutionResult) {
          val message = OperationMessage(GQL_NEXT, DataPayload(er.getData()), id)
          val jsonMessage = TextMessage(jacksonObjectMapper().writeValueAsBytes(message))
          logger.debug("Sending subscription data: {}", jsonMessage)

          if (session.isOpen) {
            session.sendMessage(jsonMessage)
            subscriptions[session.id]?.get(id)?.request(1)
          }
        }

        override fun onError(t: Throwable) {
          logger.error("Error on subscription {}", id, t)
          val message = OperationMessage(GQL_ERROR, DataPayload(null, listOf(t.message!!)), id)
          val jsonMessage = TextMessage(jacksonObjectMapper().writeValueAsBytes(message))
          logger.debug("Sending subscription error: {}", jsonMessage)

          if (session.isOpen) {
            session.sendMessage(jsonMessage)
          }
        }

        override fun onComplete() {
          logger.info("Subscription completed for {}", id)
          val message = OperationMessage(GQL_COMPLETE, null, id)
          val jsonMessage = TextMessage(jacksonObjectMapper().writeValueAsBytes(message))

          if (session.isOpen) {
            session.sendMessage(jsonMessage)
          }

          subscriptions.remove(id)
        }
      })
    }
  }
}



