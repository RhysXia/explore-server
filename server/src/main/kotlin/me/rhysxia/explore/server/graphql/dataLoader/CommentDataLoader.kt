package me.rhysxia.explore.server.graphql.dataLoader

import com.netflix.graphql.dgs.DgsDataLoader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rhysxia.explore.server.po.CommentPo
import me.rhysxia.explore.server.po.TagPo
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.service.CommentService
import me.rhysxia.explore.server.service.TagService
import me.rhysxia.explore.server.service.UserService
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

@DgsDataLoader(name = "comment")
class CommentDataLoader(private val commentService: CommentService) : MappedBatchLoader<Long, CommentPo> {
  override fun load(ids: MutableSet<Long>): CompletionStage<MutableMap<Long, CommentPo>> {
    val future = CompletableFuture<MutableMap<Long, CommentPo>>()

    GlobalScope.launch {
      val flow = commentService.findAllById(ids)
      val map = HashMap<Long, CommentPo>()

      flow.collect {
        map[it.id!!] = it
      }
      future.complete(map)
    }

    return future
  }

}