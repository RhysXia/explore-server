package me.rhysxia.explore.server.dto

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class OffsetPage(private val offset: Long, private val limit: Int, private val sort: Sort = Sort.unsorted()) :
  Pageable {
  override fun getPageNumber(): Int {
    return (offset / limit).toInt()
  }

  override fun getPageSize(): Int {
    return limit
  }

  override fun getOffset(): Long {
    return offset
  }

  override fun getSort(): Sort {
    return sort
  }

  override fun next(): Pageable {
    return OffsetPage(offset + limit, limit, sort)
  }

  override fun hasPrevious(): Boolean {
    return offset > limit
  }

  override fun previousOrFirst(): Pageable {
    val offset = (this.offset - this.limit).coerceAtLeast(0)
    return OffsetPage(offset, limit, sort)
  }

  override fun first(): Pageable {
    return OffsetPage(0, limit, sort)
  }


}