package me.rhysxia.explore.server.filter

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import me.rhysxia.explore.server.service.TokenService
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class AuthFilter(private val tokenService: TokenService) : WebFilter {
    companion object {
        const val SESSION_KEY = "__CURRENT_USER__"
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return mono {
            val session = exchange.session.awaitSingle()
            if (session.attributes[SESSION_KEY] !== null) {
                return@mono chain.filter(exchange).awaitSingle()
            }

            val req = exchange.request
            var token = req.headers.getFirst(HttpHeaders.AUTHORIZATION)
            if (token.isNullOrBlank()) {
                token = req.queryParams.getFirst("token")
            }

            if (!token.isNullOrBlank()) {
                val authUser = tokenService.findCurrentUserByToken(token)
                session.attributes[SESSION_KEY] = authUser
            }
            return@mono chain.filter(exchange).awaitSingle()
        }


    }
}