package me.rhysxia.explore.server.graphql.dataLoader

import com.netflix.graphql.dgs.DgsDataLoader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.service.ArticleService
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

@DgsDataLoader(name = "article")
class ArticleDataLoader(private val articleService: ArticleService) : MappedBatchLoader<Long, ArticlePo> {
  override fun load(ids: MutableSet<Long>): CompletionStage<MutableMap<Long, ArticlePo>> {
    val future = CompletableFuture<MutableMap<Long, ArticlePo>>()

    GlobalScope.launch {
      val flow = articleService.findAllById(ids)
      val map = HashMap<Long, ArticlePo>()

      flow.collect {
        map[it.id!!] = it
      }
      future.complete(map)
    }

    return future
  }

}