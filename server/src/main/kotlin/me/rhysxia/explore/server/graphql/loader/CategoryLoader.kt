package me.rhysxia.explore.server.graphql.loader

import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlLoader
import me.rhysxia.explore.server.po.CategoryPo
import org.dataloader.BatchLoader
import java.time.Instant
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

@GraphqlLoader("category2")
class CategoryLoader : BatchLoader<Long, CategoryPo> {
  override fun load(keys: MutableList<Long>): CompletionStage<MutableList<CategoryPo>> {
    return CompletableFuture.completedStage(mutableListOf(CategoryPo(1, "aa", "aaa", Instant.now(), Instant.now())))
  }
}