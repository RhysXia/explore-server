package me.rhysxia.explore.server.configuration.graphql.exception

import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import me.rhysxia.explore.server.exception.AuthenticationException
import me.rhysxia.explore.server.exception.AuthorizationException
import me.rhysxia.explore.server.exception.ParameterException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DefaultDataFetcherExceptionHandler : DataFetcherExceptionHandler {
  private val logger: Logger = LoggerFactory.getLogger(DefaultDataFetcherExceptionHandler::class.java)
  override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
    val exception = handlerParameters.exception
    val path = handlerParameters.path
    logger.error(
      "Exception while executing data fetcher for ${handlerParameters.path}: ${exception.message}",
      exception
    )

    val builder =
      when (exception) {
        is AuthenticationException -> {
          TypedGraphqlError.newUnauthenticatedBuilder()

        }
        is AuthorizationException -> {
          TypedGraphqlError.newPermissionDeniedBuilder()

        }
        is ParameterException -> {
          TypedGraphqlError.newBadRequestBuilder()

        }
        else -> {
          TypedGraphqlError.newInternalErrorBuilder()
        }
      }

    val graphqlError = builder.location(handlerParameters.sourceLocation)
      .path(handlerParameters.path)
      .message("%s", exception.message)
      .build()

    return DataFetcherExceptionHandlerResult.newResult()
      .error(graphqlError)
      .build()
  }
}

