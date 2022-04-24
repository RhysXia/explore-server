package me.rhysxia.explore.autoconfigure.graphql.annotations

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class GraphqlHandler(
    val parentType: String = "",
    val fieldName: String = ""
)
