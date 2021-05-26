package me.rhysxia.explore.server.configuration.graphql

import me.rhysxia.explore.server.service.TokenService
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

@Configuration
class GraphqlControllerConfiguration {

    @Bean
    fun filterRegistration(tokenService: TokenService): FilterRegistrationBean<AuthFilter> {
        val registration = FilterRegistrationBean<AuthFilter>()
        registration.filter = AuthFilter(tokenService)
        registration.order = Ordered.HIGHEST_PRECEDENCE
        registration.urlPatterns = listOf("/**")
        return registration
    }
}