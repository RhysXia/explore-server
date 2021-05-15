package me.rhysxia.explore.server.service

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.po.CategoryPo
import org.springframework.data.domain.Pageable

interface CategoryService {
  fun findAllById(ids: MutableSet<Long>): Flow<CategoryPo>
  fun findAllBy(pageable: Pageable): Flow<CategoryPo>
  suspend fun count(): Long
}
