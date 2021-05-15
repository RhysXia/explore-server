package me.rhysxia.explore.server.service

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.po.ArticlePo
import org.springframework.data.domain.Pageable

interface ArticleService {
  suspend fun countByCategoryId(categoryId: Long): Long
  fun findAllByCategoryId(categoryId: Long, pageable: Pageable): Flow<ArticlePo>
  suspend fun countByTagId(tagId: Long): Long
  fun findAllByTagId(tagId: Long, pageable: Pageable): Flow<ArticlePo>
  fun findAllById(ids: MutableSet<Long>): Flow<ArticlePo>
}
