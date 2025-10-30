package ru.practicum.android.diploma.domain.api.repositories

interface ShareRepository {

    // Поделиться вакансией
    fun shareVacancy(vacancyId: String, vacancyTitle: String)

    // Получить ссылку для шаринга
    suspend fun getShareLink(vacancyId: String): String

}
