package me.rhysxia.explore.server.graphql.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class GraphqlData(
    val parentType: String,
    val fieldName: String
)
