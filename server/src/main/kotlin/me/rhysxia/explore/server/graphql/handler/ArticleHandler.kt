package me.rhysxia.explore.server.graphql.handler

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlData
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlHandler
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlInput
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.CategoryPo
import me.rhysxia.explore.server.po.CommentPo
import me.rhysxia.explore.server.po.TagPo
import me.rhysxia.explore.server.service.ArticleService
import me.rhysxia.explore.server.service.CategoryService
import me.rhysxia.explore.server.service.CommentService
import me.rhysxia.explore.server.service.TagService
import org.springframework.data.domain.Pageable
import java.util.concurrent.CompletableFuture

@GraphqlData("Article")
class ArticleHandler(
    private val categoryService: CategoryService,
    private val tagService: TagService,
    private val articleService: ArticleService,
    private val commentService: CommentService
) {

    @GraphqlHandler
    fun category(dfe: DataFetchingEnvironment): CompletableFuture<CategoryPo> {
        val article = dfe.getSource<ArticlePo>()
        return dfe.getDataLoader<Long, CategoryPo>("category").load(article.id!!)
    }

    @GraphqlHandler
    fun tags(dfe: DataFetchingEnvironment): Flow<TagPo> {
        val article = dfe.getSource<ArticlePo>()
        return tagService.findAllByArticleId(article.id!!)
    }

    @GraphqlHandler
    fun comments(
        @GraphqlInput("top") top: Boolean,
        pageable: Pageable,
        dfe: DataFetchingEnvironment
    ): Flow<CommentPo> {
        val article = dfe.getSource<ArticlePo>()
        if (top) {
            return commentService.findAllByArticleId(article.id!!, pageable)
        }
        return commentService.findAllByArticleIdAndParentId(article.id!!, null, pageable)
    }

    @GraphqlHandler
    suspend fun commentCount(
        @GraphqlInput("top") top: Boolean,
        dfe: DataFetchingEnvironment
    ): Long {
        val article = dfe.getSource<ArticlePo>()
        if (top) {
            return commentService.countByArticleId(article.id!!)
        }
        return commentService.countByArticleIdAndParentId(article.id!!, null)
    }

}