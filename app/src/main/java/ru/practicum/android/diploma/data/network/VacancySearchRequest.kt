package ru.practicum.android.diploma.data.network

data class VacancySearchRequest(
    val text: String,
    val page: Int = 0,
    val perPage: Int = 20,
    val area: Int? = null,
    val industry: String? = null,
    val salary: Int? = null,
    val onlyWithSalary: Boolean? = null
)

fun VacancySearchRequest.toQueryMap(): Map<String, String> {
    val queryMap = mutableMapOf(
        "text" to text,
        "page" to page.toString(),
        "per_page" to perPage.toString()
    )

    area?.let { queryMap["area"] = it.toString() }
    industry?.let { queryMap["industry"] = it }
    salary?.let { queryMap["salary"] = it.toString() }
    onlyWithSalary?.let { queryMap["only_with_salary"] = it.toString() }

    println("DEBUG: Query map: $queryMap")
    return queryMap
}
