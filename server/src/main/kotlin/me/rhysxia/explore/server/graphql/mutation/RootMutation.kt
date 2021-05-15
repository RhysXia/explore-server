package me.rhysxia.explore.server.graphql.mutation

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.InputArgument
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.TagPo
import me.rhysxia.explore.server.po.UserPo
import java.util.*
import java.util.concurrent.CompletableFuture

@DgsComponent
class RootMutation {

  @DgsData(parentType = "Mutation", field = "login")
  fun login(
    @InputArgument username: String,
    @InputArgument password: String,
    dfe: DgsDataFetchingEnvironment
  ): CompletableFuture<MutableList<UserPo>> {
    val articlePo = dfe.getSource<ArticlePo>()
    val future = CompletableFuture<MutableList<TagPo>>()
    GlobalScope.launch {
      val tags = tagService.findAllByArticleId(articlePo.id!!)
      val list = LinkedList<TagPo>()

      tags.collect {
        list.add(it)
      }

      future.complete(list)
    }
    return future
  }
}