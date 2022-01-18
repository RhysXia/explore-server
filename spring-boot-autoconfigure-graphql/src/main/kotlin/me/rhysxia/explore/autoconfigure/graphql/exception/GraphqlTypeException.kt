package me.rhysxia.explore.autoconfigure.graphql.exception

open class GraphqlTypeException(message: String, cause: Throwable?) : GraphqlException(message, cause) {
  constructor(message: String) : this(message, null)
}