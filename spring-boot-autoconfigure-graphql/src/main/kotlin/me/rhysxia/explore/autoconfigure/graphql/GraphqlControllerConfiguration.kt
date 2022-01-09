package me.rhysxia.explore.autoconfigure.graphql

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router

@Configuration
class GraphqlControllerConfiguration {

//  @Bean
//  fun filterRegistration(tokenService: TokenService): FilterRegistrationBean<me.rhysxia.explore.autoconfigure.graphql.AuthFilter> {
//    val registration = FilterRegistrationBean<me.rhysxia.explore.autoconfigure.graphql.AuthFilter>()
//    registration.filter = me.rhysxia.explore.autoconfigure.graphql.AuthFilter(tokenService)
//    registration.order = Ordered.HIGHEST_PRECEDENCE
//    registration.urlPatterns = listOf("/*")
//    return registration
//  }

  @Bean
  fun graphqlRouter(
    graphqlConfigurationProperties: GraphqlConfigurationProperties, graphqlExecutionProcessor: GraphqlExecutionProcessor
  ) = router {
    accept(MediaType.APPLICATION_JSON).nest {
      POST(graphqlConfigurationProperties.query.endpoint) { req ->
        val body = req.bodyToMono<GraphqlRequestBody>().handle<Any> { body, u ->
          graphqlExecutionProcessor.doExecute(body).doOnNext {
            u.next(it.toSpecification())
          }
        }
        ok().body(body)
      }
    }
  }
}

