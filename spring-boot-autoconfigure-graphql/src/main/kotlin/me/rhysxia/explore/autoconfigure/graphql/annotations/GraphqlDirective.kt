package me.rhysxia.explore.autoconfigure.graphql.annotations

import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class GraphqlDirective(
  /**
   * directive名称，不传表示对所有地方生效
   */
  val name: String = "",
)
