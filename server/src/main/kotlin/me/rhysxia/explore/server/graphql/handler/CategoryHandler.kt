package me.rhysxia.explore.server.graphql.handler

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.core.graphql.annotation.GraphqlData
import me.rhysxia.explore.server.core.graphql.annotation.GraphqlHandler
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.CategoryPo
import me.rhysxia.explore.server.service.ArticleService
import me.rhysxia.explore.server.service.CategoryService
import me.rhysxia.explore.server.service.TagService
import org.springframework.data.domain.Pageable

@GraphqlData("Category")
class CategoryHandler(
  private val categoryService: CategoryService,
  private val tagService: TagService,
  private val articleService: ArticleService
) {

  @GraphqlHandler
  fun articles(pageable: Pageable, dfe: DataFetchingEnvironment): Flow<ArticlePo> {
    val category = dfe.getSource<CategoryPo>()
    return articleService.findAllByCategoryId(category.id!!, pageable)
  }

  @GraphqlHandler
  suspend fun articleCount(dfe: DataFetchingEnvironment): Long {
    val category = dfe.getSource<CategoryPo>()
    return articleService.countByCategoryId(category.id!!)
  }

}