package me.rhysxia.explore.server.graphql.query

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlData
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlFetcher
import me.rhysxia.explore.server.po.CategoryPo
import me.rhysxia.explore.server.service.CategoryService
import org.springframework.data.domain.Pageable

@GraphqlData("Query")
class RootQuery(private val categoryService: CategoryService) {

  @GraphqlFetcher
  fun categories(pageable: Pageable): Flow<CategoryPo> {
    return categoryService.findAllBy(pageable)
  }
}