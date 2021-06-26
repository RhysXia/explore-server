package me.rhysxia.explore.server.configuration.graphql.exception

import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
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

    val graphqlError =
      if (exception is AuthException) {
        TypedGraphqlError.newPermissionDeniedBuilder()
          .location(handlerParameters.sourceLocation)
          .path(handlerParameters.path)
          .message("%s", exception.message)
          .build()
      } else {
        TypedGraphqlError.newInternalErrorBuilder()
          .location(handlerParameters.sourceLocation)
          .path(handlerParameters.path)
          .message("%s: %s", exception::class.java.name, exception.message)
          .build()
      }

    return DataFetcherExceptionHandlerResult.newResult()
      .error(graphqlError)
      .build()
  }
}

