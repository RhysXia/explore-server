package me.rhysxia.explore.server.repository

import me.rhysxia.explore.server.po.PermissionPo
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface PermissionRepository : CoroutineSortingRepository<PermissionPo, Long> {

}