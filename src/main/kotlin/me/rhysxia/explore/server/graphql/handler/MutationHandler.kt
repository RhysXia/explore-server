package me.rhysxia.explore.server.graphql.handler

import kotlinx.coroutines.channels.Channel
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlData
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlHandler
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlInput
import me.rhysxia.explore.server.service.CategoryService
import org.springframework.stereotype.Component

@GraphqlData("Mutation")
class MutationHandler(private val categoryService: CategoryService, private val channel: MessageChannel) {

  @GraphqlHandler
  suspend fun message(@GraphqlInput("msg") msg: String): String {
    channel.send(msg)
    return msg
  }
}

@Component
class MessageChannel : Channel<String> by Channel(Channel.UNLIMITED)