package me.rhysxia.explore.server

import graphql.analysis.MaxQueryComplexityInstrumentation
import graphql.execution.instrumentation.tracing.TracingInstrumentation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@SpringBootApplication
class ServerApplication {

  @Bean
  fun tracingInstrumentation() = TracingInstrumentation()

  @Bean
  fun maxQueryComplexityInstrumentation() = MaxQueryComplexityInstrumentation(100)

  @Bean
  fun objectReactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, Any> {
    val builder = RedisSerializationContext.newSerializationContext<String, Any>()
      .key(StringRedisSerializer.UTF_8)
      .value(RedisSerializer.json())
      .hashKey(StringRedisSerializer.UTF_8)
      .hashValue(StringRedisSerializer.UTF_8)
    return ReactiveRedisTemplate(
      factory, builder.build()
    )
  }
}

fun main(args: Array<String>) {
  runApplication<ServerApplication>(*args)
}
