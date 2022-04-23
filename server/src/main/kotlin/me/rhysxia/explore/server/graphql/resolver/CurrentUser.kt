package me.rhysxia.explore.server.graphql.resolver

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class CurrentUser(
    val required: Boolean = false,
)
