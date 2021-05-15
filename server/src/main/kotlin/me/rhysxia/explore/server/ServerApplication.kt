package me.rhysxia.explore.server

import me.rhysxia.explore.server.po.ContentType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.convert.ConverterBuilder
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions

@SpringBootApplication
class ServerApplication {
  @Bean
  fun r2dbcCustomConversions(): R2dbcCustomConversions {
    val converters = ConverterBuilder
      .writing(ContentType::class.java, Int::class.java) {
        it.ordinal
      }
      .andReading { value ->
        ContentType.values().find {
          it.ordinal === value
        }
      }
      .converters
    return R2dbcCustomConversions(converters)
  }
}

fun main(args: Array<String>) {
  runApplication<ServerApplication>(*args)
}
