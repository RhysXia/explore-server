package me.rhysxia.explore.server.exception

import graphql.ErrorClassification
import graphql.GraphQLError
import graphql.execution.ResultPath
import graphql.language.SourceLocation
import java.util.*


class TypedGraphqlError(
  private val message: String,
  private val locations: List<SourceLocation>,
  private val classification: ErrorClassification,
  private val path: List<Any>,
  private val extensions: Map<String, Any>
) : GraphQLError {
  override fun getMessage(): String {
    return message
  }

  override fun getLocations(): List<SourceLocation> {
    return locations
  }

  // We return null here because we don't want graphql-java to write classification field
  override fun getErrorType(): ErrorClassification? {
    return null
  }

  override fun getPath(): List<Any> {
    return path
  }

  override fun getExtensions(): Map<String, Any> {
    return extensions
  }

  companion object {
    /**
     * Create new Builder instance to customize error.
     *
     * @return A new TypedGraphQLError.Builder instance to further customize the error.
     */
    fun newBuilder(): Builder {
      return Builder()
    }

    /**
     * Create new Builder instance to customize error.
     * @return A new TypedGraphQLError.Builder instance to further customize the error. Pre-sets ErrorType.INTERNAL.
     */
    fun newInternalErrorBuilder(): Builder {
      return Builder().errorType(ErrorType.INTERNAL)
    }

    /**
     * Create new Builder instance to customize error.
     * @return A new TypedGraphQLError.Builder instance to further customize the error. Pre-sets ErrorType.NOT_FOUND.
     */
    fun newNotFoundBuilder(): Builder {
      return Builder().errorType(ErrorType.NOT_FOUND)
    }

    /**
     * Create new Builder instance to customize error.
     * @return A new TypedGraphQLError.Builder instance to further customize the error. Pre-sets ErrorType.PERMISSION_DENIED.
     */
    fun newPermissionDeniedBuilder(): Builder {
      return Builder().errorType(ErrorType.PERMISSION_DENIED)
    }

    fun newUnauthenticatedBuilder(): Builder {
      return Builder().errorType(ErrorType.UNAUTHENTICATED)
    }

    /**
     * Create new Builder instance to customize error.
     * @return A new TypedGraphQLError.Builder instance to further customize the error. Pre-sets ErrorType.BAD_REQUEST.
     */
    fun newBadRequestBuilder(): Builder {
      return Builder().errorType(ErrorType.BAD_REQUEST)
    }
  }

  override fun toString(): String {
    return "TypedGraphQLError{" +
        "message='" + message + '\'' +
        ", locations=" + locations +
        ", path=" + path +
        ", extensions=" + extensions +
        '}'
  }

  class Builder {
    private var message: String? = null
    private var path: List<Any> = LinkedList()
    private val locations: MutableList<SourceLocation> = ArrayList()
    private var errorClassification: ErrorClassification = ErrorType.UNKNOWN
    private var extensions: Map<String, Any>? = null
    private var origin: String? = null
    private var debugUri: String? = null
    private var debugInfo: Map<String, Any>? = null
    private fun defaultMessage(): String {
      return errorClassification.toString()
    }

    private fun getExtensions(): Map<String, Any> {
      val extensionsMap = HashMap<String, Any>()
      if (extensions != null) extensionsMap.putAll(extensions!!)
      if (errorClassification is ErrorType) {
        extensionsMap["errorType"] = errorClassification.toString()
      }
      if (origin != null) extensionsMap["origin"] = origin!!
      if (debugUri != null) extensionsMap["debugUri"] = debugUri!!
      if (debugInfo != null) extensionsMap["debugInfo"] = debugInfo!!
      return extensionsMap
    }

    fun message(message: String, vararg formatArgs: String?): Builder {
      this.message = String.format((message), *formatArgs)
      return this
    }

    fun locations(locations: List<SourceLocation>): Builder {
      this.locations.addAll((locations))
      return this
    }

    fun location(location: SourceLocation): Builder {
      locations.add((location))
      return this
    }

    fun path(path: ResultPath): Builder {
      this.path = (path).toList()
      return this
    }

    fun path(path: List<Any>): Builder {
      this.path = (path)
      return this
    }

    fun errorType(errorType: ErrorType): Builder {
      errorClassification = (errorType)
      return this
    }


    fun origin(origin: String): Builder {
      this.origin = (origin)
      return this
    }

    fun debugUri(debugUri: String): Builder {
      this.debugUri = (debugUri)
      return this
    }

    fun debugInfo(debugInfo: Map<String, Any>): Builder {
      this.debugInfo = (debugInfo)
      return this
    }

    fun extensions(extensions: Map<String, Any>): Builder {
      this.extensions = (extensions)
      return this
    }

    /**
     * @return a newly built GraphQLError
     */
    fun build(): TypedGraphqlError {
      if (message == null) message = defaultMessage()
      return TypedGraphqlError(message!!, locations, errorClassification, path, getExtensions())
    }
  }
}