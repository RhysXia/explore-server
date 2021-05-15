package me.rhysxia.explore.server.graphql.dataLoader

import com.netflix.graphql.dgs.DgsDataLoader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rhysxia.explore.server.po.TagPo
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.service.TagService
import me.rhysxia.explore.server.service.UserService
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

@DgsDataLoader(name = "tag")
class TagDataLoader(private val tagService: TagService) : MappedBatchLoader<Long, TagPo> {
  override fun load(ids: MutableSet<Long>): CompletionStage<MutableMap<Long, TagPo>> {
    val future = CompletableFuture<MutableMap<Long, TagPo>>()

    GlobalScope.launch {
      val flow = tagService.findAllById(ids)
      val map = HashMap<Long, TagPo>()

      flow.collect {
        map[it.id!!] = it
      }
      future.complete(map)
    }

    return future
  }

}