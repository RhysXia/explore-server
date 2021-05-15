package me.rhysxia.explore.server.graphql.dataLoader

import com.netflix.graphql.dgs.DgsDataLoader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.service.UserService
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

@DgsDataLoader(name = "user")
class UserDataLoader(private val userService: UserService) : MappedBatchLoader<Long, UserPo> {
  override fun load(ids: MutableSet<Long>): CompletionStage<MutableMap<Long, UserPo>> {
    val future = CompletableFuture<MutableMap<Long, UserPo>>()

    GlobalScope.launch {
      val flow = userService.findAllById(ids)
      val map = HashMap<Long, UserPo>()

      flow.collect {
        map[it.id!!] = it
      }
      future.complete(map)
    }

    return future
  }

}