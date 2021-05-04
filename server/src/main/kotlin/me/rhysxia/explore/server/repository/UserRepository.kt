package me.rhysxia.explore.server.repository

import me.rhysxia.explore.server.po.UserPo
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface UserRepository : CoroutineSortingRepository<UserPo, Long> {
}