package me.rhysxia.explore.autoconfigure.graphql

import graphql.GraphQLContext
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.socket.WebSocketSession

internal const val WEBSOCKET_SESSION_KEY = "__WEBSOCKET_SESSION_KEY__"
internal const val SERVER_REQUEST_KEY = "__SERVER_REQUEST_KEY__"

fun GraphQLContext.getWebSocketSession(): WebSocketSession? = this.getOrDefault<WebSocketSession?>(WEBSOCKET_SESSION_KEY, null)

fun GraphQLContext.getServerRequest(): ServerRequest? = this.getOrDefault<ServerRequest?>(SERVER_REQUEST_KEY, null)
