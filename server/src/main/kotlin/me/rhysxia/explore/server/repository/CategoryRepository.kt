package me.rhysxia.explore.server.repository

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.po.CategoryPo
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface CategoryRepository : CoroutineSortingRepository<CategoryPo, Long> {
  fun findAllBy(pageable: Pageable): Flow<CategoryPo>
}
