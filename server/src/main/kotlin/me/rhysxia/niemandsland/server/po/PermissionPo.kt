package me.rhysxia.niemandsland.server.po

import java.util.*

data class PermissionPo(
  val id: Long?,
  //
  // 匹配模式，
  // GET: /api/1
  // POST: /api/1
  // *: /api/1
  // GET: /api/*/1
  // GET: /api/{id:\\d+}
  val pattern: String,
  /**
   * 描述
   */
  val description: String,
  /**
   * 创建时间
   */
  val createAt: Date,
  /**
   * 创建人id
   */
  val createBy: Long,
  /**
   * 更新时间
   */
  val updateAt: Date,
  /**
   * 更新人id
   */
  val updateBy: Long,
)