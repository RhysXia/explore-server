package me.rhysxia.explore.server.graphql

@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
annotation class GraphqlField(val name: String = "")
