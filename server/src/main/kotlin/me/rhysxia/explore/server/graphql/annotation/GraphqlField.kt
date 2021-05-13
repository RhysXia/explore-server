package me.rhysxia.explore.server.graphql.annotation

@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
annotation class GraphqlField(val name: String = "", val description: String = "")
