package me.rhysxia.explore.server.graphql.query

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlData
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlFetcher
import me.rhysxia.explore.server.po.CategoryPo
import java.util.concurrent.CompletableFuture

@GraphqlData("Query")
class RootQuery {

  @GraphqlFetcher
  fun categories(ctx: DataFetchingEnvironment): Flow<CategoryPo> {
    return emptyList<CategoryPo>().asFlow()
  }
}