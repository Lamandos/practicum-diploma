package ru.practicum.android.diploma.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.practicum.android.diploma.data.repositories.FilterRepositoryImpl
import ru.practicum.android.diploma.domain.api.repositories.FilterRepository
import ru.practicum.android.diploma.domain.api.usecases.FilterUseCase

val filterModule: Module = module {
    single<FilterRepository> {
        FilterRepositoryImpl()
    }

    single<FilterUseCase> {
        FilterUseCase(
            filterRepository = get()
        )
    }
}
