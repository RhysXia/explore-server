package me.rhysxia.explore.server.graphql.handler

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlData
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlHandler
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.TagPo
import me.rhysxia.explore.server.service.ArticleService
import me.rhysxia.explore.server.service.CategoryService
import me.rhysxia.explore.server.service.TagService
import org.springframework.data.domain.Pageable

@GraphqlData("Tag")
class TagHandler(
  private val categoryService: CategoryService,
  private val tagService: TagService,
  private val articleService: ArticleService
) {

  @GraphqlHandler
  fun articles(pageable: Pageable, dfe: DataFetchingEnvironment): Flow<ArticlePo> {
    val tag = dfe.getSource<TagPo>()
    return articleService.findAllByCategoryId(tag.id!!, pageable)
  }

  @GraphqlHandler
  suspend fun articleCount(dfe: DataFetchingEnvironment): Long {
    val tag = dfe.getSource<TagPo>()
    return articleService.countByTagId(tag.id!!)
  }
}