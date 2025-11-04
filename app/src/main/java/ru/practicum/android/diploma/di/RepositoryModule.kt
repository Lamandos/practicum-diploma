package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.data.repository.VacanciesRepositoryImpl
import ru.practicum.android.diploma.domain.api.repositories.VacanciesRepository

val repositoryModule = module {

    single<VacanciesRepository> {
        VacanciesRepositoryImpl(get())
    }
}
