package me.rhysxia.explore.autoconfigure.graphql

import graphql.GraphQLContext
import org.springframework.http.HttpCookie
import org.springframework.http.HttpHeaders
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.util.StringUtils
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.*
import java.util.regex.Pattern

interface SessionContainer {
    val attributes: MutableMap<String, Any>
}

interface RequestContainer {
    val attributes: MutableMap<String, Any>

    val queryParams: Map<String, List<String>>

    /**
     * 获取指定key的值list中的第一个
     */
    fun getQueryParam(name: String): String?

    val headers: HttpHeaders
    val cookies: Map<String, List<HttpCookie>>
    val session: Mono<SessionContainer>

    val originalRequest: Any
}

internal const val REQUEST_CONTAINER_KEY = "__REQUEST_CONTAINER_KEY__"

internal fun GraphQLContext.Builder.fromServerRequest(request: ServerRequest) {

    val sessionContainer: Mono<SessionContainer> = request.session().map {
        object : SessionContainer {
            override val attributes: MutableMap<String, Any>
                get() = it.attributes
        }
    }

    val container = object : RequestContainer {


        override val attributes: MutableMap<String, Any>
            get() = request.attributes()
        override val queryParams: Map<String, List<String>>
            get() = request.queryParams()

        override fun getQueryParam(name: String): String? {
            return request.queryParam(name).orElse(null)
        }

        override val headers: HttpHeaders
            get() = request.headers().asHttpHeaders()

        override val cookies: Map<String, List<HttpCookie>>
            get() = request.cookies()

        override val session: Mono<SessionContainer>
            get() = sessionContainer
        override val originalRequest: Any
            get() = request
    }

    this.of(REQUEST_CONTAINER_KEY, container)
}

internal fun GraphQLContext.Builder.fromWebSocketSession(webSocketSession: WebSocketSession) {
    val handshakeInfo = webSocketSession.handshakeInfo

    val sessionContainer: Mono<SessionContainer> = Mono.just(
        object : SessionContainer {
            override val attributes: MutableMap<String, Any>
                get() = webSocketSession.attributes

        }
    )

    val container = object : RequestContainer {
        private val QUERY_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?")

        private var innerQueryParams: Map<String, List<String>>? = null

        @Suppress("DEPRECATION")
        private fun decodeQueryParam(value: String): String {
            return try {
                URLDecoder.decode(value, "UTF-8")
            } catch (ex: UnsupportedEncodingException) {
                // Should never happen, but we got a platform default fallback anyway.
                URLDecoder.decode(value)
            }
        }

        private fun initQueryParams(): Map<String, List<String>> {
            val map: MultiValueMap<String, String> = LinkedMultiValueMap()
            val query: String? = handshakeInfo.uri.rawQuery
            if (query != null) {
                val matcher = QUERY_PATTERN.matcher(query)
                while (matcher.find()) {
                    val name: String = decodeQueryParam(matcher.group(1))
                    val eq = matcher.group(2)
                    var value = matcher.group(3)
                    value = value?.let { decodeQueryParam(it) } ?: if (StringUtils.hasLength(eq)) "" else null
                    map.add(name, value)
                }
            }
            val unmodifiableMap = Collections.unmodifiableMap(map)
            innerQueryParams = unmodifiableMap

            return unmodifiableMap
        }

        override val attributes: MutableMap<String, Any>
            get() = handshakeInfo.attributes
        override val queryParams: Map<String, List<String>>
            get() {
                if (innerQueryParams !== null) {
                    return innerQueryParams as Map<String, List<String>>
                }
                return initQueryParams()
            }

        override fun getQueryParam(name: String): String? {
            val params = queryParams[name]
            if (params.isNullOrEmpty()) {
                return null
            }
            return params[0]
        }

        override val headers: HttpHeaders
            get() = handshakeInfo.headers

        override val cookies: Map<String, List<HttpCookie>>
            get() = handshakeInfo.cookies

        override val session: Mono<SessionContainer>
            get() = sessionContainer
        override val originalRequest: Any
            get() = webSocketSession
    }

    this.of(REQUEST_CONTAINER_KEY, container)
}


fun GraphQLContext.getRequestContainer(): RequestContainer = this.get(REQUEST_CONTAINER_KEY)
