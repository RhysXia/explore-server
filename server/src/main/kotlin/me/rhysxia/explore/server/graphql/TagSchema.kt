package me.rhysxia.explore.server.graphql

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.InputArgument
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rhysxia.explore.server.graphql.types.Page
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.TagPo
import me.rhysxia.explore.server.service.ArticleService
import java.util.*
import java.util.concurrent.CompletableFuture

@DgsComponent
class TagSchema(private val articleService: ArticleService) {
  @DgsData(parentType = "Tag", field = "articleCount")
  fun articleCount(dfe: DgsDataFetchingEnvironment): CompletableFuture<Long> {
    val future = CompletableFuture<Long>()
    val tagPo = dfe.getSource<TagPo>()
    GlobalScope.launch {
      val total = articleService.countByTagId(tagPo.id!!)
      future.complete(total)
    }
    return future
  }

  @DgsData(parentType = "Tag", field = "articles")
  fun articles(
    @InputArgument page: Page,
    dfe: DgsDataFetchingEnvironment
  ): CompletableFuture<MutableList<ArticlePo>> {
    val future = CompletableFuture<MutableList<ArticlePo>>()
    val tagPo = dfe.getSource<TagPo>()
    GlobalScope.launch {
      val articles = articleService.findAllByTagId(tagPo.id!!, page.toPageable())
      val list = LinkedList<ArticlePo>()
      articles.collect {
        list.add(it)
      }
      future.complete(list)
    }
    return future
  }
}