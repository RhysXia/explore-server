package me.rhysxia.explore.server.configuration.graphql

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "graphql")
data class GraphqlConfigurator(

  val schema: Schema = Schema(),
  val query: Query = Query()
) {
  data class Schema(val location: String = "classpath*:/graphql/**/*.graphql*")

  data class Query(val endpoint: String = "/graphql")
}

