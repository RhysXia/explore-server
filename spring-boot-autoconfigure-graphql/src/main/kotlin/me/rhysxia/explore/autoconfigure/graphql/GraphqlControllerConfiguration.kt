package me.rhysxia.explore.autoconfigure.graphql

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
import reactor.kotlin.core.publisher.toMono

@Configuration
@ConditionalOnWebApplication
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
  fun schemaRouter(
    graphqlConfigurationProperties: GraphqlConfigurationProperties,
    graphqlExecutionProcessor: GraphqlExecutionProcessor
  ) = coRouter {
    (accept(MediaType.APPLICATION_JSON) and graphqlConfigurationProperties.query.endpoint).nest {
      POST("") {
        val req = it.awaitBody<GraphqlRequestBody>()
        val er = graphqlExecutionProcessor.doExecute(req).toMono().awaitSingle()
        val result = er.toSpecification()
        ok().bodyValueAndAwait(result)
      }
    }
  }

}

