package com.szastarek.gymz.shared.page

data class PageQueryParameters(
    val pageSize: PageSize,
    val pageNumber: PageNumber,
) {
    companion object {
        val default = PageQueryParameters(PageSize(20), PageNumber(1))
    }

    val offset: Int
        get() = (pageNumber.value - 1) * pageSize.value
}
