package me.rhysxia.explore.server.graphql.handler

import me.rhysxia.explore.server.core.graphql.annotation.GraphqlData
import me.rhysxia.explore.server.service.CategoryService

@GraphqlData("Subscription")
class SubscriptionHandler(private val categoryService: CategoryService)