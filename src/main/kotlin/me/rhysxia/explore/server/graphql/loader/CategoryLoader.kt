package me.rhysxia.explore.server.graphql.loader

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.rhysxia.explore.server.configuration.graphql.GraphqlMappedBatchLoader
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlLoader
import me.rhysxia.explore.server.po.CategoryPo
import me.rhysxia.explore.server.service.CategoryService

@GraphqlLoader("category")
class CategoryLoader(private val categoryService: CategoryService) : GraphqlMappedBatchLoader<Long, CategoryPo> {

  override fun load(keys: Set<Long>): Flow<Pair<Long, CategoryPo>> {
    return categoryService.findAllById(keys).map { it.id!! to it }
  }
}