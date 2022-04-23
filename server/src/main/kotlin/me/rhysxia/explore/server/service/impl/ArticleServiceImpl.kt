package me.rhysxia.explore.server.service.impl

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.repository.ArticleRepository
import me.rhysxia.explore.server.service.ArticleService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ArticleServiceImpl(private val articleRepository: ArticleRepository) : ArticleService {

    override suspend fun countByCategoryId(categoryId: Long): Long {
        return articleRepository.countByCategoryId(categoryId)
    }

    override fun findAllByCategoryId(categoryId: Long, pageable: Pageable): Flow<ArticlePo> {
        return articleRepository.findAllByCategoryId(categoryId, pageable)
    }

    override suspend fun countByTagId(tagId: Long): Long {
        return articleRepository.countByTagId(tagId)
    }

    override fun findAllByTagId(tagId: Long, pageable: Pageable): Flow<ArticlePo> {
        return articleRepository.findAllByTagId(tagId, pageable)
    }

    override fun findAllById(ids: Iterable<Long>): Flow<ArticlePo> {
        return articleRepository.findAllById(ids)
    }

    override fun findAllByAuthorId(authorId: Long, pageable: Pageable): Flow<ArticlePo> {
        return articleRepository.findAllByAuthorId(authorId, pageable)
    }
}