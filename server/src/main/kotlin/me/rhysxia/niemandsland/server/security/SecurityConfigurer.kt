package me.rhysxia.niemandsland.server.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class SecurityConfigurer : WebSecurityConfigurerAdapter() {

  override fun configure(http: HttpSecurity) {
//    http
//      .cors()
//      .disable()
//      .sessionManagement()
//      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//      .and()
//      .authorizeRequests()
//      .anyRequest()
//      .authenticated()
    http
      .authorizeRequests()
      .anyRequest()
      .permitAll()
  }
}