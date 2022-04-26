package me.rhysxia.explore.autoconfigure.graphql.loader

import org.dataloader.BatchLoader

data class BatchLoaderWrapper<ID, Entity>(
    val name: String, val loader: BatchLoader<ID, Entity>
)

