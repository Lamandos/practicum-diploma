package ru.practicum.android.diploma.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.practicum.android.diploma.domain.interactors.FavoritesInteractor
import ru.practicum.android.diploma.domain.interactors.VacancyInteractor
import ru.practicum.android.diploma.domain.interactors.impl.FavoritesInteractorImpl
import ru.practicum.android.diploma.domain.interactors.impl.VacancyInteractorImpl
import ru.practicum.android.diploma.domain.api.usecases.VacanciesInteractor
import ru.practicum.android.diploma.domain.impl.usecases.VacanciesInteractorImpl

val interactorModule: Module = module {
    factory<FavoritesInteractor> {
        FavoritesInteractorImpl(
            favoritesRepository = get()
        )
    }
    factory<VacanciesInteractor> {
        VacanciesInteractorImpl(get())
    }
    factory<VacancyInteractor> {
        VacancyInteractorImpl(
            vacancyRepository = get()
        )
    }
}
