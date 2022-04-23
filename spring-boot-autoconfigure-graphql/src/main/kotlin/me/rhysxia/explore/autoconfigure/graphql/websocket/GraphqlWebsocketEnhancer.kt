package me.rhysxia.explore.autoconfigure.graphql.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import graphql.ExecutionResult
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

class GraphqlWebsocketEnhancer private constructor(
    private val objectMapper: ObjectMapper,
    private val graphqlExecutionProcessor: GraphqlExecutionProcessor,
) {
    private lateinit var beforePair: Pair<String, String>
    private lateinit var runPair: Pair<String, String>
    private lateinit var stopReceiveCode: String

    // 客户端主动发起close session
    private var terminateReceiveCode: String? = null
    private lateinit var errorRespondCode: String
    private lateinit var completeRespondCode: String

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun execute(
        session: WebSocketSession,
    ): Mono<Void> {
        val message = session.receive()
            .map { objectMapper.readValue<OperationMessage>(it.payloadAsText) }
            .map { (type, payload, id) ->
                when (type) {
                    beforePair.first -> {
                        logger.info("Initialized connection for {}", session.id)
                        Flux.just(OperationMessage(beforePair.second))
                    }
                    runPair.first -> {
                        Flux.create { sink ->
                            val graphqlRequestBody = objectMapper.convertValue<GraphqlRequestBody>(payload!!)
                            graphqlExecutionProcessor.doExecute(graphqlRequestBody) { builder ->
                                builder.fromWebSocketSession(session)
                            }.thenApply { executionResult ->
                                executionResult.getData<Publisher<ExecutionResult>>()
                                    .subscribe(object : Subscriber<ExecutionResult> {
                                        private lateinit var subscription: Subscription
                                        override fun onSubscribe(s: Subscription) {
                                            logger.info("Subscription started for {}", id)
                                            subscription = s
                                            s.request(1)
                                        }

                                        override fun onNext(t: ExecutionResult) {
                                            val message = OperationMessage(runPair.second, t.getData(), id)
                                            if (logger.isDebugEnabled) {
                                                logger.debug(
                                                    "Sending subscription data: {}",
                                                    objectMapper.writeValueAsString(message)
                                                )
                                            }

                                            sink.next(message)

                                            if (session.isOpen) {
                                                subscription.request(1)
                                            }
                                        }

                                        override fun onError(t: Throwable) {
                                            logger.error("Error on subscription {}", id, t)
                                            val message = OperationMessage(
                                                errorRespondCode,
                                                DataPayload(null, listOf(t.message!!)),
                                                id
                                            )

                                            if (logger.isDebugEnabled) {
                                                logger.debug(
                                                    "Sending subscription data: {}",
                                                    objectMapper.writeValueAsString(message)
                                                )
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
                                session.close().subscribe()
                            }
                        } else {
                            Flux.just(OperationMessage(errorRespondCode, DataPayload(null, listOf("error")), id))
                        }
                    }
                }
            }.flatMap { a -> a }

        return session.send(message.map {
            session.textMessage(objectMapper.writeValueAsString(it))
        })
    }

    class Builder(
        objectMapper: ObjectMapper,
        graphqlExecutionProcessor: GraphqlExecutionProcessor,
    ) {

        private val enhancer: GraphqlWebsocketEnhancer =
            GraphqlWebsocketEnhancer(objectMapper, graphqlExecutionProcessor)

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
            1
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
