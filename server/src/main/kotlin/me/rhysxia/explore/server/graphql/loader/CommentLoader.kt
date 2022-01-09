package me.rhysxia.explore.server.graphql.loader

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.rhysxia.explore.server.core.graphql.GraphqlMappedBatchLoader
import me.rhysxia.explore.server.core.graphql.annotation.GraphqlLoader
import me.rhysxia.explore.server.po.CommentPo
import me.rhysxia.explore.server.service.CommentService

@GraphqlLoader("comment")
class CommentLoader(private val commentService: CommentService) : GraphqlMappedBatchLoader<Long, CommentPo> {
  override fun load(ids: Set<Long>): Flow<Pair<Long, CommentPo>> {
    return commentService.findAllById(ids).map { it.id!! to it }
  }
}