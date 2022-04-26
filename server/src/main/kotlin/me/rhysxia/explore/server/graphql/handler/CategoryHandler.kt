package me.rhysxia.explore.server.graphql.handler

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlData
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlHandler
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlInput
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlQueryHandler
import me.rhysxia.explore.server.graphql.resolver.CurrentUser
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.CategoryPo
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.service.ArticleService
import me.rhysxia.explore.server.service.CategoryService
import me.rhysxia.explore.server.service.TagService
import org.springframework.data.domain.Pageable
import java.util.concurrent.CompletableFuture

@GraphqlData("Category")
class CategoryHandler(
    private val categoryService: CategoryService,
    private val tagService: TagService,
    private val articleService: ArticleService
) {

    @GraphqlQueryHandler
    fun categories(@CurrentUser user: UserPo?, pageable: Pageable): Flow<CategoryPo> {
        return categoryService.findAllBy(pageable)
    }

    @GraphqlQueryHandler
    fun category(@GraphqlInput("id") id: Long, def: DataFetchingEnvironment): CompletableFuture<CategoryPo?> {
        val loader = def.getDataLoader<Long, CategoryPo?>("category")
        return loader.load(id)
    }

    @GraphqlQueryHandler
    suspend fun categoryCount() = categoryService.count()

    @GraphqlHandler
    fun articles(pageable: Pageable, dfe: DataFetchingEnvironment): Flow<ArticlePo> {
        val category = dfe.getSource<CategoryPo>()
        return articleService.findAllByCategoryId(category.id!!, pageable)
    }

    @GraphqlHandler
    suspend fun articleCount(dfe: DataFetchingEnvironment): Long {
        val category = dfe.getSource<CategoryPo>()
        return articleService.countByCategoryId(category.id!!)
    }

}