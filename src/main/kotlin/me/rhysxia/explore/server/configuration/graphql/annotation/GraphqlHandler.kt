package me.rhysxia.explore.server.configuration.graphql.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class GraphqlHandler(
    val parentType: String = "",
    val fieldName: String = ""
)
