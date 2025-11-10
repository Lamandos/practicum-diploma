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
}
