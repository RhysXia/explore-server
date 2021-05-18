package me.rhysxia.explore.server.configuration.graphql.annotation

import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class GraphqlLoader(
    val name: String
)
