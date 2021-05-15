package me.rhysxia.explore.server.service.impl

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.po.CommentPo
import me.rhysxia.explore.server.repository.CommentRepository
import me.rhysxia.explore.server.service.CommentService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CommentServiceImpl(private val commentRepository: CommentRepository) : CommentService {
  override fun findAllByArticleIdAndParentId(articleId: Long, parentId: Long?, pageable: Pageable): Flow<CommentPo> {
    return commentRepository.findAllByArticleIdAndParentId(articleId, parentId, pageable)
  }

  override suspend fun countByArticleId(articleId: Long): Long {
    return commentRepository.countByArticleId(articleId)
  }
  override fun findAllByArticleId(articleId: Long, pageable: Pageable): Flow<CommentPo> {
    return commentRepository.findAllByArticleId(articleId, pageable)
  }

  override suspend fun countByArticleIdAndParentId(articleId: Long, parentId: Long?): Long {
    return commentRepository.countByArticleIdAndParentId(articleId, parentId)

  }

  override fun findAllById(ids: MutableSet<Long>): Flow<CommentPo> {
    return commentRepository.findAllById(ids)

  }

}