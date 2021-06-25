package me.rhysxia.explore.server.configuration.graphql.websocket

data class DataPayload(val data: Any?, val errors: List<Any>? = emptyList())