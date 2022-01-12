package me.rhysxia.explore.server.graphql.scalar

import graphql.language.IntValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlScalar
import java.time.Instant

@GraphqlScalar("Timestamp")
class TimestampScalar : Coercing<Instant, Long> {
  override fun serialize(dataFetcherResult: Any): Long {
    return if (dataFetcherResult is Instant) {
      dataFetcherResult.toEpochMilli()
    } else {
      throw CoercingSerializeException("Not a valid Timestamp")
    }
  }

  override fun parseValue(input: Any): Instant {
    if (input is Long) {
      return Instant.ofEpochMilli(input)
    } else if (input is Int) {
      return Instant.ofEpochMilli(input.toLong())
    }
    throw CoercingParseValueException("Expected a 'number' but was '${input.javaClass.name}'.")
  }

  override fun parseLiteral(input: Any): Instant {
    if ((input is IntValue)) {
      return Instant.ofEpochMilli(input.value.toLong())
    }
    throw CoercingParseLiteralException("Expected AST type 'IntValue' but was ''${input.javaClass.name}'.")
  }
}