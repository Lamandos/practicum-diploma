package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.practicum.android.diploma.presentation.details.VacancyViewModel
import ru.practicum.android.diploma.presentation.favorites.viewmodel.FavoritesViewModel
import ru.practicum.android.diploma.presentation.search.viewmodel.SearchViewModel

val viewModelModule: Module = module {
    viewModel {
        SearchViewModel(get())
    }
    viewModel {
        FavoritesViewModel(
            favoritesInteractor = get()
        )
    }
    viewModel {
        VacancyViewModel(
            vacancyInteractor = get(),
            favoritesInteractor = get()
        )
    }
}

