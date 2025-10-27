package ru.practicum.android.diploma.data.network

import ru.practicum.android.diploma.data.dto.Response

class RetrofitNetworkClient(
    private val vacancySearchApiService: VacancySearchApiService,
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        TODO("Not yet implemented")
    }
}
