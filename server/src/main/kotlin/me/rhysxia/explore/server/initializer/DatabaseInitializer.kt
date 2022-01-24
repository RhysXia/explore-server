package me.rhysxia.explore.server.initializer

import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOne
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils

@Component
class DatabaseInitializer(private val databaseClient: DatabaseClient) : ApplicationRunner {

  /**
   * 是否已经初始化过了
   */
  private suspend fun isInitialized(): Boolean {
    val spec = databaseClient.sql("select count(1) from pg_class where relname = 'sys_user'").fetch()

    val row = spec.awaitOne()

    return (row["count"] as Long) > 0
  }


  override fun run(args: ApplicationArguments) = runBlocking {
    val schemaFile = ResourceUtils.getFile("classpath*:/schema.sql")

    if (isInitialized()) {
      return@runBlocking
    }


  }
}