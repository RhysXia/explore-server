package me.rhysxia.explore.autoconfigure.graphql.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import graphql.ExecutionResult
import me.rhysxia.explore.autoconfigure.graphql.GraphqlExecutionProcessor
import me.rhysxia.explore.autoconfigure.graphql.GraphqlRequestBody
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

class GraphqlTWSHandler(
  private val objectMapper: ObjectMapper,
  private val graphqlExecutionProcessor: GraphqlExecutionProcessor
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
    val message = session.receive().map { it.payloadAsText }.map {
      val (type, payload, id) = objectMapper.readValue<OperationMessage>(it)
      when (type) {
        GQL_CONNECTION_INIT -> {
          logger.info("Initialized connection for {}", session.id)
          Flux.just(OperationMessage(GQL_CONNECTION_ACK))
        }
        GQL_SUBSCRIBE -> {
          Flux.create { sink ->
            val graphqlRequestBody = objectMapper.convertValue<GraphqlRequestBody>(payload!!)
            graphqlExecutionProcessor.doExecute(graphqlRequestBody).thenApply { executionResult ->
              executionResult.getData<Publisher<ExecutionResult>>().subscribe(object : Subscriber<ExecutionResult> {
                private lateinit var subscription: Subscription
                override fun onSubscribe(s: Subscription) {
                  logger.info("Subscription started for {}", id)
                  subscription = s
                  s.request(1)
                }

                override fun onNext(t: ExecutionResult) {
                  val message = OperationMessage(GQL_NEXT, t.getData(), id)
                  if (logger.isDebugEnabled) {
                    logger.debug("Sending subscription data: {}", objectMapper.writeValueAsString(message))
                  }

                  sink.next(message)

                  if (session.isOpen) {
                    subscription.request(1)
                  }
                }

                override fun onError(t: Throwable) {
                  logger.error("Error on subscription {}", id, t)
                  val message = OperationMessage(GQL_ERROR, DataPayload(null, listOf(t.message!!)), id)

                  if (logger.isDebugEnabled) {
                    logger.debug("Sending subscription data: {}", objectMapper.writeValueAsString(message))
                  }

                  sink.next(message)

                  sink.error(t)
                }

                override fun onComplete() {
                  logger.info("Subscription completed for {}", id)
                  val message = OperationMessage(GQL_COMPLETE, null, id)
                  sink.next(message)
                  sink.complete()
                }
              })
            }
          }
        }
        GQL_STOP -> {
          Flux.empty()
        }
        GQL_COMPLETE -> {
          logger.info("Terminated session " + session.id)
          Flux.empty<OperationMessage>().doOnComplete {
            session.close()
          }
        }
        else -> {
          Flux.just(OperationMessage(GQL_ERROR, DataPayload(null, listOf("error")), id))
        }
      }
    }.flatMap { a -> a }

    return session.send(message.map { session.textMessage(objectMapper.writeValueAsString(it)) })
  }

  override fun getSubProtocols(): List<String> {
    return listOf("graphql-transport-ws")
  }

}


