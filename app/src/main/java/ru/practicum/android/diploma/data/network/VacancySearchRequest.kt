package ru.practicum.android.diploma.data.network

data class VacancySearchRequest(
    val text: String,
    val page: Int = 0,
    val perPage: Int = 20,
    val area: Int? = null,
    val industry: String? = null,
    val salaryfrom: Int? = null,
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

    // ВАЖНО: Не передаем salary в API, так как фильтруем на клиенте
    // Только включаем only_with_salary если отмечен чекбокс
    onlyWithSalary?.let {
        queryMap["only_with_salary"] = it.toString()
        println("DEBUG: Only with salary filter: $it")
    }

    println("DEBUG: Query map (client-side salary filtering): $queryMap")
    return queryMap
}
