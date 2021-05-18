package me.rhysxia.explore.server.configuration.graphql

import kotlinx.coroutines.flow.Flow

interface GraphqlMappedBatchLoader<ID, Entity> {
    fun load(keys: List<ID>): Flow<Entity>
}