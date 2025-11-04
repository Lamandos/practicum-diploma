package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.presentation.details.VacancyViewModel
import ru.practicum.android.diploma.presentation.search.viewmodel.SearchViewModel

val viewModelModule = module {
    viewModel {
        SearchViewModel(get())
    }
    viewModel {
        VacancyViewModel()
    }
}
