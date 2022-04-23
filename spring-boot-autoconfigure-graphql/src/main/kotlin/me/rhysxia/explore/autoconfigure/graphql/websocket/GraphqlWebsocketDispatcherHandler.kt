package me.rhysxia.explore.autoconfigure.graphql.websocket

import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class GraphqlWebsocketDispatcherHandler(private val handlers: List<WebSocketHandler>) : WebSocketHandler {

    override fun handle(session: WebSocketSession): Mono<Void> {
        val subProtocol = session.handshakeInfo.subProtocol
        for (handler in handlers) {
            if (subProtocol in handler.subProtocols) {
                return handler.handle(session)
            }
        }
        return session.send(Flux.just(session.textMessage("Not support protocol")))
    }

    override fun getSubProtocols(): List<String> {
        return handlers.map { it.subProtocols }.flatten()
    }
}