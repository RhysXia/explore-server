package me.rhysxia.explore.server.graphql.loader

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlLoader
import me.rhysxia.explore.autoconfigure.graphql.interfaces.GraphqlMappedBatchLoader
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.service.ArticleService
import org.springframework.stereotype.Component

@GraphqlLoader("article")
@Component
class ArticleLoader(private val articleService: ArticleService) : GraphqlMappedBatchLoader<Long, ArticlePo> {
    override fun load(keys: Set<Long>): Flow<Pair<Long, ArticlePo>> {
        return articleService.findAllById(keys).map { it.id!! to it }
    }
}