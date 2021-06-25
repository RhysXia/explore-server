package me.rhysxia.explore.server.service

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.po.CommentPo
import org.springframework.data.domain.Pageable

interface CommentService {
  fun findAllByArticleIdAndParentId(articleId: Long, parentId: Long?, pageable: Pageable): Flow<CommentPo>
  fun findAllByArticleId(articleId: Long, pageable: Pageable): Flow<CommentPo>
  suspend fun countByArticleIdAndParentId(articleId: Long, parentId: Long?): Long
  suspend fun countByArticleId(articleId: Long): Long
  fun findAllById(ids: Set<Long>): Flow<CommentPo>
}
