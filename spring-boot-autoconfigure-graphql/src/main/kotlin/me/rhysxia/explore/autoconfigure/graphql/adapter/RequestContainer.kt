package me.rhysxia.explore.autoconfigure.graphql.adapter

import org.springframework.http.HttpHeaders

interface SessionContainer {
  val attributes: MutableMap<Any, Any>
}

abstract class RequestContainer {
  abstract val attributes: MutableMap<Any, Any>
  abstract val queryParams: MutableMap<Any, Any>
  abstract val headers: HttpHeaders
  abstract val cookies


}