package me.rhysxia.explore.server.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import me.rhysxia.explore.server.po.TokenPo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext


@Configuration
class RedisConfiguration {

    @Bean
    fun tokenReactiveRedisTemplate(
        factory: ReactiveRedisConnectionFactory, objectMapper: ObjectMapper
    ): ReactiveRedisTemplate<String, TokenPo> {

        val serializer = Jackson2JsonRedisSerializer(TokenPo::class.java)
        serializer.setObjectMapper(objectMapper)

        val builder = RedisSerializationContext.newSerializationContext<String, TokenPo>(serializer)
        return ReactiveRedisTemplate(
            factory, builder.build()
        )
    }
}