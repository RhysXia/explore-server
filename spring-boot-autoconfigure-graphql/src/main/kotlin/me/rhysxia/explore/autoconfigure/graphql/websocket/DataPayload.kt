package me.rhysxia.explore.autoconfigure.graphql.websocket

data class DataPayload(
    val data: Any?,
    val errors: List<Any>? = emptyList()
)