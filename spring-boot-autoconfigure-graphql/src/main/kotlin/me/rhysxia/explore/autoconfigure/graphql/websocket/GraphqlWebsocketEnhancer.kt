package me.rhysxia.explore.autoconfigure.graphql.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import graphql.ExecutionResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.rhysxia.explore.autoconfigure.graphql.GraphqlConfigurationProperties
import me.rhysxia.explore.autoconfigure.graphql.GraphqlExecutionProcessor
import me.rhysxia.explore.autoconfigure.graphql.GraphqlRequestBody
import me.rhysxia.explore.autoconfigure.graphql.fromWebSocketSession
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

class GraphqlWebsocketEnhancer private constructor() {
  private lateinit var beforePair: Pair<String, String>
  private lateinit var runPair: Pair<String, String>
  private lateinit var stopReceiveCode: String

  // 客户端主动发起close session
  private var terminateReceiveCode: String? = null
  private lateinit var errorRespondCode: String
  private lateinit var completeRespondCode: String

  private val logger = LoggerFactory.getLogger(this.javaClass)

  private val initJobMap: MutableMap<String, Job> = ConcurrentHashMap()

  fun execute(
    session: WebSocketSession,
    objectMapper: ObjectMapper,
    graphqlExecutionProcessor: GraphqlExecutionProcessor,
    graphqlConfigurationProperties: GraphqlConfigurationProperties
  ): Mono<Void> {
    val message = session.receive()
      .map { objectMapper.readValue<OperationMessage>(it.payloadAsText) }
      .map { (type, payload, id) ->
        when (type) {
          beforePair.first -> {
            logger.info("Initialized connection for {}", session.id)
            Flux.create { sink ->
              sink.next(OperationMessage(beforePair.second))
              val job = GlobalScope.launch {
                delay(graphqlConfigurationProperties.subscription.connectionInitWaitTimeout)
                if (!sink.isCancelled && session.isOpen) {
                  sink.next(OperationMessage(errorRespondCode, listOf("4408: Connection initialisation timeout")))
                  sink.complete()
                }
                initJobMap.remove(id)
              }
              initJobMap[id!!] = job
            }
          }
          runPair.first -> {
            initJobMap.remove(id)?.cancel()
            Flux.create { sink ->
              val graphqlRequestBody = objectMapper.convertValue<GraphqlRequestBody>(payload!!)
              graphqlExecutionProcessor.doExecute(graphqlRequestBody) { builder ->
                builder.fromWebSocketSession(session)
              }.thenApply { executionResult ->
                executionResult.getData<Publisher<ExecutionResult>>().subscribe(object : Subscriber<ExecutionResult> {
                  private lateinit var subscription: Subscription
                  override fun onSubscribe(s: Subscription) {
                    logger.info("Subscription started for {}", id)
                    subscription = s
                    s.request(1)
                  }

                  override fun onNext(t: ExecutionResult) {
                    val message = OperationMessage(runPair.second, t.getData(), id)
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
                    val message = OperationMessage(errorRespondCode, DataPayload(null, listOf(t.message!!)), id)

                    if (logger.isDebugEnabled) {
                      logger.debug("Sending subscription data: {}", objectMapper.writeValueAsString(message))
                    }

                    sink.next(message)

                    sink.error(t)
                  }

                  override fun onComplete() {
                    logger.info("Subscription completed for {}", id)
                    val message = OperationMessage(completeRespondCode, null, id)
                    sink.next(message)
                    sink.complete()
                  }
                })
              }
            }
          }
          stopReceiveCode -> {
            Flux.empty()
          }
          else -> {
            if (terminateReceiveCode !== null && type === terminateReceiveCode) {
              logger.info("Terminated session " + session.id)
              Flux.empty<OperationMessage>().doOnComplete {
                session.close()
              }
            } else {
              Flux.just(OperationMessage(errorRespondCode, DataPayload(null, listOf("error")), id))

            }
          }
        }
      }.flatMap { a -> a }

    return session.send(message.map { session.textMessage(objectMapper.writeValueAsString(it)) })
  }

  class Builder {

    private val enhancer: GraphqlWebsocketEnhancer = GraphqlWebsocketEnhancer()

    fun before(receiveCode: String, respondCode: String): Builder {
      enhancer.beforePair = receiveCode to respondCode
      return this
    }

    fun run(receiveCode: String, respondCode: String): Builder {
      enhancer.runPair = receiveCode to respondCode
      return this
    }

    fun stopReceiveCode(respondCode: String): Builder {
      enhancer.stopReceiveCode = respondCode
      return this
    }

    fun errorRespondCode(respondCode: String): Builder {
      enhancer.errorRespondCode = respondCode
      return this
    }

    fun completeRespondCode(respondCode: String): Builder {
      enhancer.completeRespondCode = respondCode
      return this
    }

    fun terminateReceiveCode(respondCode: String): Builder {
      enhancer.terminateReceiveCode = respondCode
      return this
    }

    fun build(): GraphqlWebsocketEnhancer {
      return enhancer
    }

  }
}