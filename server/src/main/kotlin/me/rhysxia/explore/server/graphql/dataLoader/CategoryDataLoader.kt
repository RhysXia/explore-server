package me.rhysxia.explore.server.graphql.dataLoader

import com.netflix.graphql.dgs.DgsDataLoader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rhysxia.explore.server.po.CategoryPo
import me.rhysxia.explore.server.service.CategoryService
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

@DgsDataLoader(name = "category")
class CategoryDataLoader(private val categoryService: CategoryService) : MappedBatchLoader<Long, CategoryPo> {
  override fun load(ids: MutableSet<Long>): CompletionStage<MutableMap<Long, CategoryPo>> {
    val future = CompletableFuture<MutableMap<Long, CategoryPo>>()

    GlobalScope.launch {
      val flow = categoryService.findAllById(ids)
      val map = HashMap<Long, CategoryPo>()

      flow.collect {
        map[it.id!!] = it
      }
      future.complete(map)
    }

    return future
  }

}