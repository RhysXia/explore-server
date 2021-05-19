package me.rhysxia.explore.server.graphql.loader

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.configuration.graphql.GraphqlBatchLoader
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlLoader
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.service.ArticleService

@GraphqlLoader("article")
class ArticleLoader(private val articleService: ArticleService) : GraphqlBatchLoader<Long, ArticlePo> {
  override fun load(ids: MutableList<Long>): Flow<ArticlePo> {
    return articleService.findAllById(ids)
  }
}