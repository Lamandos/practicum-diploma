package ru.practicum.android.diploma.domain.interactors

import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.network.FilterAreaResponse
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.models.vacancy.Country

class CountriesRepositoryImpl(
    private val networkClient: NetworkClient
) : CountriesRepository {

    override suspend fun getAllCountries(): List<Country> {
        val response = networkClient.getAreas()
        return if (response is ResponseSuccess<*>) {
            val data = response.data as? FilterAreaResponse
            data?.areas?.map { Country(id = it.id, name = it.name) } ?: emptyList()
        } else {
            emptyList()
        }
    }
}
