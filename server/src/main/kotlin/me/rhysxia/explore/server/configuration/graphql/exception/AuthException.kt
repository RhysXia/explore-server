package me.rhysxia.explore.server.configuration.graphql.exception

import java.lang.RuntimeException

class AuthException(message: String, cause: Throwable?): RuntimeException(message, cause) {
    constructor(message: String) : this(message, null)
}