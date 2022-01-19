package me.rhysxia.explore.autoconfigure.graphql

import graphql.GraphQLContext
import org.springframework.http.HttpCookie
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.server.WebSession

interface SessionContainer {
  val attributes: MutableMap<String, Any>
}

interface RequestContainer {
  val attributes: MutableMap<String, Any>

  val headers: HttpHeaders
  val cookies: Map<String, List<HttpCookie>>
  val session: SessionContainer

  val originalRequest: Any
}

internal const val REQUEST_CONTAINER_KEY = "__REQUEST_CONTAINER_KEY__"

internal fun GraphQLContext.Builder.fromServerRequest(request: ServerRequest, webSession: WebSession) {
  val container = object : RequestContainer {
    override val attributes: MutableMap<String, Any>
      get() = request.attributes()

    override val headers: HttpHeaders
      get() = request.headers().asHttpHeaders()

    override val cookies: Map<String, List<HttpCookie>>
      get() = request.cookies()

    override val session: SessionContainer
      get() = object : SessionContainer {
        override val attributes: MutableMap<String, Any>
          get() = webSession.attributes
      }
    override val originalRequest: Any
      get() = request
  }

  this.of(REQUEST_CONTAINER_KEY, container)
}

internal fun GraphQLContext.Builder.fromWebSocketSession(webSocketSession: WebSocketSession) {
  val container = object : RequestContainer {
    override val attributes: MutableMap<String, Any>
      get() = webSocketSession.handshakeInfo.attributes

    override val headers: HttpHeaders
      get() = webSocketSession.handshakeInfo.headers

    override val cookies: Map<String, List<HttpCookie>>
      get() = webSocketSession.handshakeInfo.cookies

    override val session: SessionContainer
      get() = object : SessionContainer {
        override val attributes: MutableMap<String, Any>
          get() = webSocketSession.attributes

      }
    override val originalRequest: Any
      get() = webSocketSession
  }

  this.of(REQUEST_CONTAINER_KEY, container)
}


fun GraphQLContext.getRequestContainer() = this.get<RequestContainer>(REQUEST_CONTAINER_KEY)
