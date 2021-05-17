package me.rhysxia.explore.server.graphql.query

import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlData
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlFetcher
import me.rhysxia.explore.server.po.CategoryPo

@GraphqlData("Query")
class RootQuery {

    @GraphqlFetcher
    fun categories(): List<CategoryPo> {
        return emptyList()
    }
}