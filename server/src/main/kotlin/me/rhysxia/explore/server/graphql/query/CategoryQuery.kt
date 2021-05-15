package me.rhysxia.explore.server.graphql.query

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.InputArgument
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rhysxia.explore.server.graphql.types.Page
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.CategoryPo
import me.rhysxia.explore.server.service.ArticleService
import org.springframework.data.domain.PageRequest
import java.util.*
import java.util.concurrent.CompletableFuture

@DgsComponent
class CategoryQuery(private val articleService: ArticleService) {
  @DgsData(parentType = "Category", field = "articleCount")
  fun articleCount(dfe: DgsDataFetchingEnvironment): CompletableFuture<Long> {
    val future = CompletableFuture<Long>()
    val categoryPo = dfe.getSource<CategoryPo>()
    GlobalScope.launch {
      val total = articleService.countByCategoryId(categoryPo.id!!)
      future.complete(total)
    }
    return future
  }

  @DgsData(parentType = "Category", field = "articles")
  fun articles(
    @InputArgument page: Page,
    dfe: DgsDataFetchingEnvironment
  ): CompletableFuture<MutableList<ArticlePo>> {
    val future = CompletableFuture<MutableList<ArticlePo>>()
    val categoryPo = dfe.getSource<CategoryPo>()
    GlobalScope.launch {
      val articles = articleService.findAllByCategoryId(categoryPo.id!!, page.toPageable())
      val list = LinkedList<ArticlePo>()
      articles.collect {
        list.add(it)
      }
      future.complete(list)
    }
    return future
  }
}