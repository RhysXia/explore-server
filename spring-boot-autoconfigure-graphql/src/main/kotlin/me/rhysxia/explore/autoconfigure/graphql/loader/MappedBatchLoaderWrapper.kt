package me.rhysxia.explore.autoconfigure.graphql.loader

import org.dataloader.MappedBatchLoader

data class MappedBatchLoaderWrapper<ID, Entity>(
    val name: String, val loader: MappedBatchLoader<ID, Entity>
)