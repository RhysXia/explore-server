package me.rhysxia.explore.server.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
class SecurityConfigurer : WebSecurityConfigurerAdapter() {

  override fun configure(http: HttpSecurity) {
    http
//      .cors()
//      .disable()
      .csrf()
      .disable()
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .authorizeRequests()
      .anyRequest()
      .permitAll()
  }
}