package me.rhysxia.explore.server.graphql

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.InputArgument
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rhysxia.explore.server.graphql.dataLoader.ArticleDataLoader
import me.rhysxia.explore.server.graphql.dataLoader.CommentDataLoader
import me.rhysxia.explore.server.graphql.dataLoader.UserDataLoader
import me.rhysxia.explore.server.graphql.types.Page
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.CommentPo
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.service.CommentService
import java.util.*
import java.util.concurrent.CompletableFuture

@DgsComponent
class CommentSchema(private val commentService: CommentService) {
  @DgsData(parentType = "Comment", field = "author")
  fun author(dfe: DgsDataFetchingEnvironment): CompletableFuture<UserPo> {
    val commentPo = dfe.getSource<CommentPo>()
    val loader = dfe.getDataLoader<Long, UserPo>(UserDataLoader::class.java)
    return loader.load(commentPo.authorId)
  }

  @DgsData(parentType = "Comment", field = "article")
  fun article(dfe: DgsDataFetchingEnvironment): CompletableFuture<ArticlePo> {
    val commentPo = dfe.getSource<CommentPo>()
    val loader = dfe.getDataLoader<Long, ArticlePo>(ArticleDataLoader::class.java)
    return loader.load(commentPo.articleId)
  }

  @DgsData(parentType = "Comment", field = "parent")
  fun parent(
    dfe: DgsDataFetchingEnvironment
  ): CompletableFuture<CommentPo?> {
    val commentPo = dfe.getSource<CommentPo>()
    if (commentPo.parentId === null) {
      return CompletableFuture.supplyAsync { null }
    }
    val loader = dfe.getDataLoader<Long, CommentPo>(CommentDataLoader::class.java)
    return loader.load(commentPo.parentId)
  }

  @DgsData(parentType = "Comment", field = "childrenCount")
  fun childrenCount(
    dfe: DgsDataFetchingEnvironment
  ): CompletableFuture<Long> {
    val commentPo = dfe.getSource<CommentPo>()
    val future = CompletableFuture<Long>()
    GlobalScope.launch {
      val total = commentService.countByArticleIdAndParentId(
        commentPo.articleId,
        commentPo.id!!,
      )
      future.complete(total)
    }
    return future
  }

  @DgsData(parentType = "Comment", field = "children")
  fun children(
    @InputArgument page: Page,
    dfe: DgsDataFetchingEnvironment
  ): CompletableFuture<MutableList<CommentPo>> {
    val commentPo = dfe.getSource<CommentPo>()
    val future = CompletableFuture<MutableList<CommentPo>>()
    GlobalScope.launch {
      val flow = commentService.findAllByArticleIdAndParentId(
        commentPo.articleId,
        commentPo.id!!,
        page.toPageable()
      )
      val list = LinkedList<CommentPo>()
      flow.collect {
        list.add(it)
      }
      future.complete(list)
    }
    return future
  }
}