package me.rhysxia.explore.server.graphql.handler

import kotlinx.coroutines.flow.flow
import me.rhysxia.explore.server.configuration.graphql.annotation.CurrentUser
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlData
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlHandler
import me.rhysxia.explore.server.dto.OffsetPage
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.service.CategoryService

@GraphqlData("Subscription")
class SubscriptionHandler(private val categoryService: CategoryService, private val channel: MessageChannel) {

  @GraphqlHandler
  fun messages(@CurrentUser user: UserPo?) = flow {
    println(user)
    for (msg in channel) {
      emit(msg)
    }
  }

  @GraphqlHandler
  fun category() = categoryService.findAllBy(OffsetPage(0, 10))
}