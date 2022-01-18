package me.rhysxia.explore.server.repository

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.po.CommentPo
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface CommentRepository : CoroutineSortingRepository<CommentPo, Long> {

  fun findAllByArticleIdAndParentId(articleId: Long, parentId: Long?, pageable: Pageable): Flow<CommentPo>
  fun findAllByArticleId(articleId: Long, pageable: Pageable): Flow<CommentPo>
  suspend fun countByArticleIdAndParentId(articleId: Long, parentId: Long?): Long
  suspend fun countByArticleId(articleId: Long): Long
}
