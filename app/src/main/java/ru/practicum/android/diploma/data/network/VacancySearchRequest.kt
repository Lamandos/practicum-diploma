package ru.practicum.android.diploma.data.network

data class VacancySearchRequest(
    val text: String,
    val page: Int = 0,
    val perPage: Int = 20
)
fun VacancySearchRequest.toQueryMap(): Map<String, String> {
    return mapOf(
        "text" to text,
        "page" to page.toString(),
        "per_page" to perPage.toString()
    )
}
