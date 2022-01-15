package me.rhysxia.explore.autoconfigure.graphql.websocket

import me.rhysxia.explore.autoconfigure.graphql.exception.GraphqlException
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession

class GraphqlWebsocketDispatcherHandler(private val handlerMap: Map<String, WebSocketHandler>) :
  WebSocketHandler {


  private fun getActualHandler(session: WebSocketSession): WebSocketHandler {
    val protocol = session.acceptedProtocol
    return handlerMap[protocol] ?: throw GraphqlException("Not support websocket protocol '$protocol'")
  }

  override fun afterConnectionEstablished(session: WebSocketSession) {
    getActualHandler(session).afterConnectionEstablished(session)
  }

  override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
    getActualHandler(session).handleMessage(session, message)
  }

  override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
    getActualHandler(session).handleTransportError(session, exception)
  }

  override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
    getActualHandler(session).afterConnectionClosed(session, closeStatus)
  }

  override fun supportsPartialMessages(): Boolean {
    return false
  }
}