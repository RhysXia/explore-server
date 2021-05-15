package me.rhysxia.explore.server.graphql.types

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

data class Order(
  val property: String,
  val direction: Sort.Direction
)

data class Page(
  val page: Int,
  val size: Int,
  val sort: List<Order> = emptyList()
) {
  fun toPageable(): Pageable {
    val orders = this.sort.map { Sort.Order(it.direction, it.property) }
    return PageRequest.of(this.page, this.size, Sort.by(orders))
  }
}
