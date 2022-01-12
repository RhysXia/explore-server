package me.rhysxia.explore.server.graphql.handler

import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlData
import me.rhysxia.explore.server.service.CategoryService

@GraphqlData("Subscription")
class SubscriptionHandler(private val categoryService: CategoryService)