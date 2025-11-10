package ru.practicum.android.diploma.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.practicum.android.diploma.data.repositories.AreasRepository
import ru.practicum.android.diploma.data.repositories.FavoritesRepositoryImpl
import ru.practicum.android.diploma.data.repositories.SearchVacanciesRepositoryImpl
import ru.practicum.android.diploma.data.repositories.VacancyRepositoryImpl
import ru.practicum.android.diploma.domain.api.repositories.FavoritesRepository
import ru.practicum.android.diploma.domain.api.repositories.VacanciesRepository
import ru.practicum.android.diploma.domain.api.repositories.VacancyRepository
import ru.practicum.android.diploma.domain.interactors.CountriesRepository
import ru.practicum.android.diploma.domain.interactors.CountriesRepositoryImpl
import ru.practicum.android.diploma.domain.interactors.impl.RegionsRepository
import ru.practicum.android.diploma.domain.interactors.impl.RegionsRepositoryImpl

val repositoryModule: Module = module {
    single<FavoritesRepository> {
        FavoritesRepositoryImpl(
            database = get(),
            mappers = get()
        )
    }
    single<VacancyRepository> {
        VacancyRepositoryImpl(
            networkClient = get()
        )
    }
    single<VacanciesRepository> {
        SearchVacanciesRepositoryImpl(get())
    }
    single { AreasRepository(get()) }
    single<RegionsRepository> { RegionsRepositoryImpl(get()) }
    single<CountriesRepository> { CountriesRepositoryImpl(get()) }
    single { IndustriesRepository(get()) }
}
