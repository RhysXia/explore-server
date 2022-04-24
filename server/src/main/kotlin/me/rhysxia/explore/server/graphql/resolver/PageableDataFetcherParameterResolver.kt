package me.rhysxia.explore.server.graphql.resolver

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetchingEnvironment
import me.rhysxia.explore.autoconfigure.graphql.interfaces.GraphqlDataFetcherParameterResolver
import me.rhysxia.explore.server.dto.OffsetPage
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

@Component
class PageableDataFetcherParameterResolver(private val objectMapper: ObjectMapper) :
    GraphqlDataFetcherParameterResolver<Pageable> {
    override fun support(parameter: KParameter): Boolean {
        val javaType = parameter.type.javaType

        if (javaType is Class<*>) {
            return javaType.isAssignableFrom(Pageable::class.java)
        }

        return false
    }

    override fun resolve(dfe: DataFetchingEnvironment, parameter: KParameter): Mono<Pageable> {

        val offset = dfe.getArgumentOrDefault("offset", 0L)
        val limit = dfe.getArgumentOrDefault("limit", 10)
        val orders = dfe.getArgumentOrDefault<List<Map<String, String>>>("sort", emptyList()).mapNotNull {
            val property = it["property"]
            val direction = it["direction"]
            if (property === null || direction === null) {
                return@mapNotNull null
            }
            Sort.Order(Sort.Direction.fromString(direction), property)
        }

        val sort = Sort.by(orders)

        val pageable = OffsetPage(offset, limit, sort)

        return Mono.just(pageable)
    }
}