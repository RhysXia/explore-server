package me.rhysxia.explore.server.initializer

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator


@Configuration
class DatabaseInitializer {

  @Bean
  fun initializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
    val initializer = ConnectionFactoryInitializer()
    initializer.setConnectionFactory(connectionFactory)
    val populator = CompositeDatabasePopulator(
      ResourceDatabasePopulator(ClassPathResource("sql/schema.sql")),
      ResourceDatabasePopulator(ClassPathResource("sql/data.sql"))
    )
    initializer.setDatabasePopulator {
      mono {
        try {
          val result = it.createStatement("SELECT version FROM sys_info LIMIT 1").execute().awaitSingle()
          val row = result.map { t, _ -> t }.awaitSingle()
          println(row)
        } catch (e: Exception) {
          populator.populate(it).awaitSingleOrNull()
        }
        return@mono null
      }
    }
    return initializer
  }
}