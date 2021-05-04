package me.rhysxia.explore.server.repository

import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface RoleRepository : CoroutineSortingRepository<RoleRepository, Long> {

}