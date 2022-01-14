package me.rhysxia.explore.autoconfigure.graphql.exception

open class GraphqlException(message: String, cause: Throwable?) : RuntimeException(message, cause) {
  constructor(message: String) : this(message, null)
}