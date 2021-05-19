package me.rhysxia.explore.server.graphql.query

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlData
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlFetcher
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.CategoryPo
import me.rhysxia.explore.server.service.ArticleService
import me.rhysxia.explore.server.service.CategoryService
import me.rhysxia.explore.server.service.TagService
import org.springframework.data.domain.Pageable
import java.util.concurrent.CompletableFuture

@GraphqlData("Query")
class RootQuery(
  private val categoryService: CategoryService,
  private val tagService: TagService,
  private val articleService: ArticleService
) {

  @GraphqlFetcher
  fun categories(pageable: Pageable): Flow<CategoryPo> {
    return categoryService.findAllBy(pageable)
  }

  @GraphqlFetcher
  suspend fun categoryCount() = categoryService.count()

  @GraphqlFetcher
  fun tags(pageable: Pageable) = tagService.findAllBy(pageable)

  @GraphqlFetcher
  suspend fun tagCount() = tagService.count()

  @GraphqlFetcher
  fun article(id: Long, def: DataFetchingEnvironment): CompletableFuture<ArticlePo> {
    val loader = def.getDataLoader<Long, ArticlePo>("article")
    return loader.load(id)
  }
}