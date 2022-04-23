package me.rhysxia.explore.autoconfigure.graphql

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "graphql")
data class GraphqlConfigurationProperties(
    val schema: Schema = Schema(),
    val query: Query = Query(),
    val subscription: Subscription = Subscription(),
) {
    data class Schema(val location: String = "classpath*:/graphql/**/*.graphql*")

    data class Query(val endpoint: String = "/graphql")
    data class Subscription(
        val endpoint: String = "/subscription",
    )
}

