package me.rhysxia.explore.server.security

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SecurityConfig {

  @Bean
  fun loginFilterRegister(securityProperties: SecurityProperties): FilterRegistrationBean<TokenFilter> {
    val tokenFilter = TokenFilter(securityProperties)
    val registrationBean = FilterRegistrationBean(tokenFilter)
    registrationBean.addUrlPatterns("/*")
    registrationBean.order = FilterRegistrationBean.HIGHEST_PRECEDENCE
    return registrationBean
  }
}