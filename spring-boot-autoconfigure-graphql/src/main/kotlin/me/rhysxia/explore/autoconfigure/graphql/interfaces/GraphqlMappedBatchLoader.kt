package me.rhysxia.explore.autoconfigure.graphql.interfaces

import kotlinx.coroutines.flow.Flow

interface GraphqlMappedBatchLoader<ID, Entity> {
  fun load(keys: Set<ID>): Flow<Pair<ID, Entity>>
}
