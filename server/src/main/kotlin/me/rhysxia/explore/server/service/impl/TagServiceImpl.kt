package me.rhysxia.explore.server.service.impl

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.po.TagPo
import me.rhysxia.explore.server.repository.TagRepository
import me.rhysxia.explore.server.service.TagService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class TagServiceImpl(private val tagRepository: TagRepository) : TagService {
    override suspend fun count(): Long {
        return tagRepository.count()
    }

    override fun findAllBy(pageable: Pageable): Flow<TagPo> {
        return tagRepository.findAllBy(pageable)
    }

    override fun findAllByArticleId(articleId: Long): Flow<TagPo> {
        return tagRepository.findAllByArticleId(articleId)
    }

    override fun findAllById(ids: MutableSet<Long>): Flow<TagPo> {
        return tagRepository.findAllById(ids)
    }
}