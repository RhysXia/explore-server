package me.rhysxia.explore.autoconfigure.graphql.annotations

import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class GraphqlData(
    val parentType: String,
)
