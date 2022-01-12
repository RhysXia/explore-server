package me.rhysxia.explore.server.graphql.handler

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlData
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlHandler
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.service.ArticleService
import me.rhysxia.explore.server.service.CategoryService
import me.rhysxia.explore.server.service.CommentService
import me.rhysxia.explore.server.service.TagService
import org.springframework.data.domain.Pageable

@GraphqlData("User")
class UserHandler(
  private val categoryService: CategoryService,
  private val tagService: TagService,
  private val articleService: ArticleService,
  private val commentService: CommentService
) {

  @GraphqlHandler
  fun articles(pageable: Pageable, dfe: DataFetchingEnvironment): Flow<ArticlePo> {
    val user = dfe.getSource<UserPo>()
    return articleService.findAllByAuthorId(user.id!!, pageable)
  }


}