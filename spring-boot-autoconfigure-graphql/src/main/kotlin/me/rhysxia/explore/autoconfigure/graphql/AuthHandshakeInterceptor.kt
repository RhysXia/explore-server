//package me.rhysxia.explore.autoconfigure.graphql
//
//import me.rhysxia.explore.server.dto.AuthUser
//import me.rhysxia.explore.server.service.TokenService
//import org.springframework.http.server.ServerHttpRequest
//import org.springframework.http.server.ServerHttpResponse
//import org.springframework.http.server.ServletServerHttpRequest
//import org.springframework.web.socket.WebSocketHandler
//import org.springframework.web.socket.server.HandshakeInterceptor
//
//class AuthHandshakeInterceptor(private val tokenService: TokenService) : HandshakeInterceptor {
//  override fun beforeHandshake(
//    request: ServerHttpRequest,
//    response: ServerHttpResponse,
//    wsHandler: WebSocketHandler,
//    attributes: MutableMap<String, Any>
//  ): Boolean {
//
//    if (request is ServletServerHttpRequest) {
//      val user =
//        request.servletRequest.getAttribute(me.rhysxia.explore.autoconfigure.graphql.AuthFilter.USER_KEY) as AuthUser?
//      if (user !== null) {
//        attributes[me.rhysxia.explore.autoconfigure.graphql.AuthFilter.USER_KEY] = user
//      }
//    }
//
//    return true
//  }
//
//  override fun afterHandshake(
//    request: ServerHttpRequest,
//    response: ServerHttpResponse,
//    wsHandler: WebSocketHandler,
//    exception: Exception?
//  ) {
//  }
//}