package ru.practicum.android.diploma.domain.interactors.impl

import ru.practicum.android.diploma.domain.interactors.CountriesRepository
import ru.practicum.android.diploma.domain.models.vacancy.Country

class CountriesInteractorImpl(
    private val repository: CountriesRepository
) : CountriesInteractor {

    override suspend fun getCountries(): List<Country> {
        return repository.getAllCountries()
    }
}
