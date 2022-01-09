package me.rhysxia.explore.server.graphql.loader

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.rhysxia.explore.server.core.graphql.GraphqlMappedBatchLoader
import me.rhysxia.explore.server.core.graphql.annotation.GraphqlLoader
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.service.UserService

@GraphqlLoader("user")
class UserLoader(private val userService: UserService) : GraphqlMappedBatchLoader<Long, UserPo> {
  override fun load(ids: Set<Long>): Flow<Pair<Long, UserPo>> {
    return userService.findAllById(ids).map { it.id!! to it }
  }
}