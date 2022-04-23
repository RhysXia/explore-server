package me.rhysxia.explore.server.po

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("category")
data class CategoryPo(
    @Id
    val id: Long?,
    val name: String,
    val description: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)
