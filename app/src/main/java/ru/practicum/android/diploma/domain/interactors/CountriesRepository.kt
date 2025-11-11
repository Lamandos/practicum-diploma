package ru.practicum.android.diploma.domain.interactors

import ru.practicum.android.diploma.domain.models.vacancy.Country

interface CountriesRepository {
    suspend fun getAllCountries(): List<Country>
}
