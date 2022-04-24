package me.rhysxia.explore.autoconfigure.graphql

import org.dataloader.BatchLoader
import org.dataloader.MappedBatchLoader

data class BatchLoaderWrapper<ID, Entity>(
    val name: String, val loader: BatchLoader<ID, Entity>
)

data class MappedBatchLoaderWrapper<ID, Entity>(
    val name: String, val loader: MappedBatchLoader<ID, Entity>
)