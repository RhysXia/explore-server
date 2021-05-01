package me.rhysxia.niemandsland.server.po

import java.util.*

data class RolePo(
  val id: Long?,
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