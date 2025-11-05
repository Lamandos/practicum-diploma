package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.domain.api.usecases.VacanciesInteractor
import ru.practicum.android.diploma.domain.impl.usecases.VacanciesInteractorImpl
import ru.practicum.android.diploma.domain.interactor.VacancyInteractor

val interactorModule = module {
    single { VacancyInteractor(get()) }

    factory<VacanciesInteractor> {
        VacanciesInteractorImpl(get())
    }
}
