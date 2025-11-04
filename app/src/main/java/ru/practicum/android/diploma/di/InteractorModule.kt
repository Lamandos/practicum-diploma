package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.domain.api.usecases.VacanciesInteractor
import ru.practicum.android.diploma.domain.impl.usecases.VacanciesInteractorImpl

val interactorModule = module {

    factory<VacanciesInteractor> {
        VacanciesInteractorImpl(get())
    }
}
