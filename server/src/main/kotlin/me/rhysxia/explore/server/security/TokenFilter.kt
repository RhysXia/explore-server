package me.rhysxia.explore.server.security

import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TokenFilter(private val securityProperties: SecurityProperties) : OncePerRequestFilter() {
  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    var token = request.getHeader(securityProperties.tokenName)
    if (token === null) {
      token = request.getParameter(securityProperties.tokenName)
    }
    if (token !== null) {
      request.setAttribute(securityProperties.tokenInternalName, token)
    }
    filterChain.doFilter(request, response)
  }
}