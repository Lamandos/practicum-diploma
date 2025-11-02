package ru.practicum.android.diploma.data.network

data class VacancySearchRequest(
    val text: String,
    val page: Int = 0,
    val perPage: Int = 20
) {
    fun toQueryMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        map["text"] = text
        map["page"] = page.toString()
        map["per_page"] = perPage.toString()
        return map
    }
}
