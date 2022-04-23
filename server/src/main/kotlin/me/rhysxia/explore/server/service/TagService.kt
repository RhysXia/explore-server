package me.rhysxia.explore.server.service

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.po.TagPo
import org.springframework.data.domain.Pageable

interface TagService {
    suspend fun count(): Long
    fun findAllBy(pageable: Pageable): Flow<TagPo>
    fun findAllById(ids: MutableSet<Long>): Flow<TagPo>
    fun findAllByArticleId(articleId: Long): Flow<TagPo>
}
