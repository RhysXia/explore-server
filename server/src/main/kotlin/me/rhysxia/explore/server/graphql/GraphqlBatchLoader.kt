package me.rhysxia.explore.server.graphql

import org.dataloader.BatchLoader

interface GraphqlBatchLoader<K,V>: BatchLoader<K,V> {
  fun getName(): String
}