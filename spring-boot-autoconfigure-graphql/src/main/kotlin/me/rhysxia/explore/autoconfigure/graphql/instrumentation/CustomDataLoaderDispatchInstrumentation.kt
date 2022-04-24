package me.rhysxia.explore.autoconfigure.graphql.instrumentation

import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory

class CustomDataLoaderDispatchInstrumentation : DataLoaderDispatcherInstrumentation() {

    private val log = LoggerFactory.getLogger(CustomDataLoaderDispatchInstrumentation::class.java)


    override fun instrumentDataFetcher(
        dataFetcher: DataFetcher<*>,
        parameters: InstrumentationFieldFetchParameters
    ): DataFetcher<*> {

        return DataFetcher { environment: DataFetchingEnvironment? ->
            val obj: Any = dataFetcher.get(environment)
            parameters.executionContext.dataLoaderRegistry.dispatchAll()
            obj
        }

    }
}


