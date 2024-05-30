package com.szastarek.gymz.shared.page

import io.ktor.server.application.ApplicationCall
import kotlinx.serialization.Serializable

@Serializable
data class Page<T>(
    val data: List<T>,
    val totalElements: PageTotalElements,
    val pageSize: PageSize,
    val pageNumber: PageNumber,
    val isLastPage: Boolean,
) {
    fun <U> map(transform: (T) -> U): Page<U> = Page(
        data = data.map(transform),
        totalElements = totalElements,
        pageSize = pageSize,
        pageNumber = pageNumber,
        isLastPage = isLastPage,
    )
}

fun ApplicationCall.getPageParameters(
    defaultPageSize: PageSize = PageSize(20),
    defaultPage: PageNumber = PageNumber(1),
): PageQueryParameters {
    val pageSize = parameters["pageSize"]?.toIntOrNull()?.let { PageSize(it) } ?: defaultPageSize
    val pageNumber = parameters["pageNumber"]?.toIntOrNull()?.let { PageNumber(it) } ?: defaultPage
    return PageQueryParameters(pageSize, pageNumber)
}
