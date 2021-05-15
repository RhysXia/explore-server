package me.rhysxia.explore.server.graphql.query

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.InputArgument
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rhysxia.explore.server.graphql.dataLoader.ArticleDataLoader
import me.rhysxia.explore.server.graphql.dataLoader.CategoryDataLoader
import me.rhysxia.explore.server.graphql.dataLoader.TagDataLoader
import me.rhysxia.explore.server.graphql.types.Page
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.CategoryPo
import me.rhysxia.explore.server.po.TagPo
import me.rhysxia.explore.server.service.CategoryService
import me.rhysxia.explore.server.service.TagService
import java.util.concurrent.CompletableFuture

@DgsComponent
class RootQuery(
  private val categoryService: CategoryService,
  private val tagService: TagService
) {
  @DgsData(parentType = "Query", field = "categories")
  fun categories(@InputArgument page: Page): CompletableFuture<MutableList<CategoryPo>> {
    val future = CompletableFuture<MutableList<CategoryPo>>()
    GlobalScope.launch {
      val list = ArrayList<CategoryPo>()
      val flow = categoryService.findAllBy(page.toPageable())
      flow.collect {
        list.add(it)
      }
      future.complete(list)
    }
    return future
  }

  @DgsData(parentType = "Query", field = "categoryCount")
  fun categoryCount(): CompletableFuture<Long> {
    val future = CompletableFuture<Long>()
    GlobalScope.launch {
      val count = categoryService.count()
      future.complete(count)
    }
    return future
  }

  @DgsData(parentType = "Query", field = "tagCount")
  fun tagCount(): CompletableFuture<Long> {
    val future = CompletableFuture<Long>()
    GlobalScope.launch {
      val count = tagService.count()
      future.complete(count)
    }
    return future
  }

  @DgsData(parentType = "Query", field = "tags")
  fun tags(@InputArgument page: Page): CompletableFuture<MutableList<TagPo>> {
    val future = CompletableFuture<MutableList<TagPo>>()
    GlobalScope.launch {
      val list = ArrayList<TagPo>()
      val flow = tagService.findAllBy(page.toPageable())
      flow.collect {
        list.add(it)
      }
      future.complete(list)
    }
    return future
  }

  @DgsData(parentType = "Query", field = "article")
  fun article(@InputArgument id: Long, dfe: DgsDataFetchingEnvironment): CompletableFuture<ArticlePo> {
    val loader = dfe.getDataLoader<Long, ArticlePo>(ArticleDataLoader::class.java)
    return loader.load(id)
  }

  @DgsData(parentType = "Query", field = "category")
  fun category(@InputArgument id: Long, dfe: DgsDataFetchingEnvironment): CompletableFuture<CategoryPo> {
    val loader = dfe.getDataLoader<Long, CategoryPo>(CategoryDataLoader::class.java)
    return loader.load(id)
  }

  @DgsData(parentType = "Query", field = "tag")
  fun tag(@InputArgument id: Long, dfe: DgsDataFetchingEnvironment): CompletableFuture<TagPo> {
    val loader = dfe.getDataLoader<Long, TagPo>(TagDataLoader::class.java)
    return loader.load(id)
  }
}