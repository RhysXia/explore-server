package me.rhysxia.explore.server.configuration.graphql.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class GraphqlData(
    val parentType: String = "",
)
