package me.rhysxia.explore.server.graphql.handler

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlData
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlHandler
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.TagPo
import me.rhysxia.explore.server.service.ArticleService
import me.rhysxia.explore.server.service.CategoryService
import me.rhysxia.explore.server.service.TagService
import org.springframework.data.domain.Pageable

@GraphqlData("Tag")
class TagHandler(
    private val categoryService: CategoryService,
    private val tagService: TagService,
    private val articleService: ArticleService
) {

    @GraphqlHandler(parentType = "Query")
    fun tags(pageable: Pageable) = tagService.findAllBy(pageable)

    @GraphqlHandler(parentType = "Query")
    suspend fun tagCount() = tagService.count()
    @GraphqlHandler
    fun articles(pageable: Pageable, dfe: DataFetchingEnvironment): Flow<ArticlePo> {
        val tag = dfe.getSource<TagPo>()
        return articleService.findAllByCategoryId(tag.id!!, pageable)
    }

    @GraphqlHandler
    suspend fun articleCount(dfe: DataFetchingEnvironment): Long {
        val tag = dfe.getSource<TagPo>()
        return articleService.countByTagId(tag.id!!)
    }
}