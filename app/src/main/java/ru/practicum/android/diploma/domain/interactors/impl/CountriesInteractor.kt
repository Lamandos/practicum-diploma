package ru.practicum.android.diploma.domain.interactors.impl

import ru.practicum.android.diploma.domain.models.vacancy.Country

interface CountriesInteractor {
    suspend fun getCountries(): List<Country>
}
