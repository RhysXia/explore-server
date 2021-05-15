package me.rhysxia.explore.server.graphql.query

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.InputArgument
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rhysxia.explore.server.graphql.dataLoader.CategoryDataLoader
import me.rhysxia.explore.server.graphql.dataLoader.UserDataLoader
import me.rhysxia.explore.server.graphql.types.Page
import me.rhysxia.explore.server.po.*
import me.rhysxia.explore.server.service.CommentService
import me.rhysxia.explore.server.service.TagService
import java.util.*
import java.util.concurrent.CompletableFuture

@DgsComponent
class ArticleQuery(private val tagService: TagService, private val commentService: CommentService) {
  @DgsData(parentType = "Article", field = "category")
  fun category(
    dfe: DgsDataFetchingEnvironment
  ): CompletableFuture<CategoryPo> {
    val articlePo = dfe.getSource<ArticlePo>()
    val loader = dfe.getDataLoader<Long, CategoryPo>(CategoryDataLoader::class.java)

    return loader.load(articlePo.categoryId)
  }

  @DgsData(parentType = "Article", field = "author")
  fun author(
    dfe: DgsDataFetchingEnvironment
  ): CompletableFuture<UserPo> {
    val articlePo = dfe.getSource<ArticlePo>()
    val loader = dfe.getDataLoader<Long, UserPo>(UserDataLoader::class.java)
    return loader.load(articlePo.authorId)
  }

  @DgsData(parentType = "Article", field = "tags")
  fun tag(
    dfe: DgsDataFetchingEnvironment
  ): CompletableFuture<MutableList<TagPo>> {
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

  @DgsData(parentType = "Article", field = "comments")
  fun comments(
    @InputArgument top: Boolean,
    @InputArgument page: Page,
    dfe: DgsDataFetchingEnvironment
  ): CompletableFuture<MutableList<CommentPo>> {
    val articlePo = dfe.getSource<ArticlePo>()
    val future = CompletableFuture<MutableList<CommentPo>>()
    GlobalScope.launch {

      val comments = if (top) commentService.findAllByArticleIdAndParentId(
        articlePo.id!!,
        null,
        page.toPageable()
      ) else commentService.findAllByArticleId(articlePo.id!!, page.toPageable())
      val list = LinkedList<CommentPo>()

      comments.collect {
        list.add(it)
      }

      future.complete(list)
    }
    return future
  }

  @DgsData(parentType = "Article", field = "commentCount")
  fun articleCount(
    @InputArgument top: Boolean,
    dfe: DgsDataFetchingEnvironment
  ): CompletableFuture<Long> {
    val articlePo = dfe.getSource<ArticlePo>()
    val future = CompletableFuture<Long>()
    GlobalScope.launch {
      val total = if (top) commentService.countByArticleIdAndParentId(
        articlePo.id!!,
        null
      ) else commentService.countByArticleId(articlePo.id!!)
      future.complete(total)
    }
    return future
  }
}