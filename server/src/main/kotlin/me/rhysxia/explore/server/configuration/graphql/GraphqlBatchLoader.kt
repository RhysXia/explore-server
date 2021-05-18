package me.rhysxia.explore.server.configuration.graphql

import kotlinx.coroutines.flow.Flow

interface GraphqlBatchLoader<ID, Entity> {
    fun load(keys: List<ID>): Flow<Entity?>
}