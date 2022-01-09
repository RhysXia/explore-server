package me.rhysxia.explore.server.repository

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.po.TagPo
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface TagRepository : CoroutineSortingRepository<TagPo, Long> {
  fun findAllBy(pageable: Pageable): Flow<TagPo>

  @Query("SELECT * FROM Tag t WHERE t.id in (SELECT tag_id FROM rel_tag_article rta WHERE rta.article_id = :articleId)")
  fun findAllByArticleId(articleId: Long): Flow<TagPo>
}
