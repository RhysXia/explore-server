package me.rhysxia.explore.server.graphql.query

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlData
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlHandler
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.CommentPo
import me.rhysxia.explore.server.service.ArticleService
import me.rhysxia.explore.server.service.CategoryService
import me.rhysxia.explore.server.service.CommentService
import me.rhysxia.explore.server.service.TagService
import org.springframework.data.domain.Pageable
import java.util.concurrent.CompletableFuture

@GraphqlData("Comment")
class CommentQuery(
  private val categoryService: CategoryService,
  private val tagService: TagService,
  private val articleService: ArticleService,
  private val commentService: CommentService
) {

  @GraphqlHandler
  fun article(dfe: DataFetchingEnvironment): CompletableFuture<ArticlePo> {
    val comment = dfe.getSource<CommentPo>()
    return dfe.getDataLoader<Long, ArticlePo>("article").load(comment.id!!)
  }

  @GraphqlHandler
  fun author(dfe: DataFetchingEnvironment): CompletableFuture<ArticlePo> {
    val comment = dfe.getSource<CommentPo>()
    return dfe.getDataLoader<Long, ArticlePo>("user").load(comment.id!!)
  }

  @GraphqlHandler
  fun parent(dfe: DataFetchingEnvironment): CompletableFuture<CommentPo> {
    val comment = dfe.getSource<CommentPo>()
    return dfe.getDataLoader<Long, CommentPo>("comment").load(comment.id!!)
  }

  @GraphqlHandler
  fun children(pageable: Pageable, dfe: DataFetchingEnvironment): Flow<CommentPo> {
    val comment = dfe.getSource<CommentPo>()
    return commentService.findAllByArticleIdAndParentId(comment.articleId, comment.id!!, pageable)
  }

  @GraphqlHandler
  suspend fun childrenCount(dfe: DataFetchingEnvironment): Long {
    val comment = dfe.getSource<CommentPo>()
    return commentService.countByArticleIdAndParentId(comment.articleId, comment.id!!)
  }

}