package me.rhysxia.explore.autoconfigure.graphql.adapter

import org.springframework.web.reactive.socket.WebSocketSession

class WebSocketSessionRequestContainer(private val webSocketSession: WebSocketSession) {
  val attributes = webSocketSession.attributes

  fun a() {
    webSocketSession.handshakeInfo.
  }
}