package me.rhysxia.explore.server.graphql.handler

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlData
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlHandler
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlInput
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlQuery
import me.rhysxia.explore.server.graphql.resolver.CurrentUser
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.CategoryPo
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.service.ArticleService
import me.rhysxia.explore.server.service.CategoryService
import me.rhysxia.explore.server.service.TagService
import org.springframework.data.domain.Pageable
import java.util.concurrent.CompletableFuture

@GraphqlQuery
class QueryHandler(
  private val categoryService: CategoryService,
  private val tagService: TagService,
  private val articleService: ArticleService
) {

  @GraphqlHandler
  fun currentUser(@CurrentUser user: UserPo?) = user

  @GraphqlHandler
  fun categories(@CurrentUser user: UserPo?, pageable: Pageable): Flow<CategoryPo> {
    return categoryService.findAllBy(pageable)
  }

  @GraphqlHandler
  suspend fun categoryCount() = categoryService.count()

  @GraphqlHandler
  fun tags(pageable: Pageable) = tagService.findAllBy(pageable)

  @GraphqlHandler
  suspend fun tagCount() = tagService.count()

  @GraphqlHandler
  fun article(@GraphqlInput("id") id: Long, def: DataFetchingEnvironment): CompletableFuture<ArticlePo> {
    val loader = def.getDataLoader<Long, ArticlePo>("article")
    return loader.load(id)
  }
}