package ru.practicum.android.diploma.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.practicum.android.diploma.data.repositories.FilterRepositoryImpl
import ru.practicum.android.diploma.data.storage.FilterPreferences
import ru.practicum.android.diploma.domain.api.repositories.FilterRepository
import ru.practicum.android.diploma.domain.api.usecases.FilterInteractor
import ru.practicum.android.diploma.domain.api.usecases.FilterInteractorImpl
import ru.practicum.android.diploma.presentation.filter.viewmodel.FilterViewModel

val filterModule: Module = module {
    single { FilterPreferences(androidContext()) }

    single<FilterRepository> {
        FilterRepositoryImpl(filterPreferences = get())
    }

    single<FilterInteractor> {
        FilterInteractorImpl(filterRepository = get())
    }

    factory { FilterViewModel(filterInteractor = get()) }
}
