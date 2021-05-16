package me.rhysxia.explore.server.graphql.annotation

import org.springframework.stereotype.Component

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class GraphqlInput(
    val name: String
)
