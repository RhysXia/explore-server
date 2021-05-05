package me.rhysxia.explore.server.security

import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LoginFilter(private val securityProperties: SecurityProperties) : OncePerRequestFilter() {
  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    val token = request.getAttribute(securityProperties.tokenInternalName)
    if(token !== null) {

    }
    filterChain.doFilter(request, response)
  }
}