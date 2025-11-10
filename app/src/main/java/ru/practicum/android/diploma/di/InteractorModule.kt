package ru.practicum.android.diploma.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.practicum.android.diploma.domain.interactors.FavoritesInteractor
import ru.practicum.android.diploma.domain.interactors.SearchVacanciesInteractor
import ru.practicum.android.diploma.domain.interactors.VacancyInteractor
import ru.practicum.android.diploma.domain.interactors.impl.CountriesInteractor
import ru.practicum.android.diploma.domain.interactors.impl.FavoritesInteractorImpl
import ru.practicum.android.diploma.domain.interactors.impl.RegionsInteractor
import ru.practicum.android.diploma.domain.interactors.impl.RegionsInteractorImpl
import ru.practicum.android.diploma.domain.interactors.impl.SearchVacanciesInteractorImpl
import ru.practicum.android.diploma.domain.interactors.impl.VacancyInteractorImpl
import ru.practicum.android.diploma.domain.interactors.impl.CountriesInteractorImpl

val interactorModule: Module = module {
    factory<FavoritesInteractor> {
        FavoritesInteractorImpl(
            favoritesRepository = get(),
            mappers = get()
        )
    }
    factory<SearchVacanciesInteractor> {
        SearchVacanciesInteractorImpl(get())
    }
    factory<CountriesInteractor> { CountriesInteractorImpl(get()) }
    factory<RegionsInteractor> { RegionsInteractorImpl(get()) }
    factory<VacancyInteractor> {
        VacancyInteractorImpl(
            vacancyRepository = get()
        )
    }
}
