package me.rhysxia.explore.server.configuration.graphql

import kotlinx.coroutines.runBlocking
import me.rhysxia.explore.server.service.TokenService
import org.springframework.http.HttpHeaders
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthFilter(private val tokenService: TokenService) : OncePerRequestFilter() {
    companion object {
        const val USER_KEY = "__USER_KEY__"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var token: String? = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (!token.isNullOrEmpty()) {
            runBlocking {
                val authUser = tokenService.findAuthUserByToken(token)
                request.setAttribute(USER_KEY, authUser)
            }
        }
        filterChain.doFilter(request, response)
    }
}