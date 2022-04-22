package me.rhysxia.explore.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
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

  @Bean
  fun objectMapper(): ObjectMapper {
    val mapper = JsonMapper.builder() // or different mapper for other format
      .addModule(Jdk8Module())
      .addModule(JavaTimeModule())
      .addModule(KotlinModule.Builder().build())
      .build();

    return mapper
  }
}

fun main(args: Array<String>) {
  runApplication<ServerApplication>(*args)
}
