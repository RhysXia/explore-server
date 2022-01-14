package me.rhysxia.explore.server.graphql.handler

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlData
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlHandler
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlSubscription
import me.rhysxia.explore.server.service.CategoryService

@GraphqlSubscription
class SubscriptionHandler(private val categoryService: CategoryService) {

  @GraphqlHandler
  fun message() = flow<String> {
    for (i in 0..10) {
      emit(i.toString())
      delay(2000)
    }
  }

}