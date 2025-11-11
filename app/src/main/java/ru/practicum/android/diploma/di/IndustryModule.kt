package ru.practicum.android.diploma.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.practicum.android.diploma.data.repositories.IndustryRepositoryImpl
import ru.practicum.android.diploma.domain.api.repositories.IndustryRepository
import ru.practicum.android.diploma.domain.interactors.IndustryInteractor
import ru.practicum.android.diploma.domain.interactors.impl.IndustryInteractorImpl

val industryModule: Module = module {
    single<IndustryRepository> {
        IndustryRepositoryImpl(networkClient = get())
    }

    single<IndustryInteractor> {
        IndustryInteractorImpl(industriesRepository = get())
    }
}
