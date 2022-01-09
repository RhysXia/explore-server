package me.rhysxia.explore.autoconfigure.graphql.interfaces

import kotlinx.coroutines.flow.Flow

interface GraphqlBatchLoader<ID, Entity> {
  fun load(keys: List<ID>): Flow<Entity?>
}