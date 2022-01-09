package me.rhysxia.explore.server.service.impl

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.po.CategoryPo
import me.rhysxia.explore.server.repository.CategoryRepository
import me.rhysxia.explore.server.service.CategoryService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CategoryServiceImpl(private val categoryRepository: CategoryRepository) : CategoryService {
  override fun findAllById(ids: Set<Long>): Flow<CategoryPo> {
    return categoryRepository.findAllById(ids)
  }

  override fun findAllBy(pageable: Pageable): Flow<CategoryPo> {
    return categoryRepository.findAllBy(pageable)
  }

  override suspend fun count(): Long {
    return categoryRepository.count()
  }
}