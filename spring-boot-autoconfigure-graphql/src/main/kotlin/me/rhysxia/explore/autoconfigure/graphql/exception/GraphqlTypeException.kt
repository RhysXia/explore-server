package me.rhysxia.explore.autoconfigure.graphql.exception

import graphql.GraphQLException

open class GraphqlTypeException(message: String, cause: Throwable?) : GraphQLException(message, cause) {
  constructor(message: String) : this(message, null)
}