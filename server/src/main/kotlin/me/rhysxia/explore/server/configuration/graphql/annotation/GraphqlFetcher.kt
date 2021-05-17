package me.rhysxia.explore.server.configuration.graphql.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class GraphqlFetcher(
    val parentType: String = "",
    val fieldName: String = ""
)
