package me.rhysxia.explore.server.graphql.handler

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlData
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlHandler
import me.rhysxia.explore.server.graphql.resolver.CurrentUser
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.CategoryPo
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.service.ArticleService
import me.rhysxia.explore.server.service.CategoryService
import me.rhysxia.explore.server.service.TagService
import org.springframework.data.domain.Pageable

@GraphqlData("Category")
class CategoryHandler(
    private val categoryService: CategoryService,
    private val tagService: TagService,
    private val articleService: ArticleService
) {

    @GraphqlHandler(parentType = "Query")
    fun categories(@CurrentUser user: UserPo?, pageable: Pageable): Flow<CategoryPo> {
        return categoryService.findAllBy(pageable)
    }

    @GraphqlHandler(parentType = "Query")
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