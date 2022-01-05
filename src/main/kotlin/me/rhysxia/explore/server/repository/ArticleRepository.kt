package me.rhysxia.explore.server.repository

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.po.ArticlePo
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface ArticleRepository : CoroutineSortingRepository<ArticlePo, Long> {
  suspend fun countByCategoryId(categoryId: Long): Long
  fun findAllByCategoryId(categoryId: Long, pageable: Pageable): Flow<ArticlePo>

  @Query("SELECT count(a.id) FROM Article a WHERE a.id in (SELECT article_id FROM rel_tag_article rta WHERE rta.tag_id = :tagId)")
  suspend fun countByTagId(tagId: Long): Long

  @Query("SELECT * FROM Article a WHERE a.id in (SELECT article_id FROM rel_tag_article rta WHERE rta.tag_id = :tagId)")
  fun findAllByTagId(tagId: Long, pageable: Pageable): Flow<ArticlePo>

  fun findAllByAuthorId(authorId: Long, pageable: Pageable): Flow<ArticlePo>
}
