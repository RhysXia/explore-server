package me.rhysxia.explore.server.init

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@Component
class DatabaseInit(private val mapping: RequestMappingHandlerMapping) : ApplicationRunner {
  override fun run(args: ApplicationArguments) {
    val patters = getAllPatterns()
  }

  private fun getAllPatterns() {
    val map = mapping.handlerMethods
  }
}