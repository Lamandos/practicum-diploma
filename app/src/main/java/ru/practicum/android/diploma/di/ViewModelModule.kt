package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.practicum.android.diploma.presentation.details.VacancyViewModel
import ru.practicum.android.diploma.presentation.favorites.viewmodel.FavoritesViewModel
import ru.practicum.android.diploma.presentation.filter.viewmodel.ChooseCountryViewModel
import ru.practicum.android.diploma.presentation.filter.viewmodel.ChooseIndustryViewModel
import ru.practicum.android.diploma.presentation.filter.viewmodel.ChooseRegionViewModel
import ru.practicum.android.diploma.presentation.filter.viewmodel.ChooseWorkPlaceViewModel
import ru.practicum.android.diploma.presentation.filter.viewmodel.FilterViewModel
import ru.practicum.android.diploma.presentation.search.viewmodel.SearchViewModel

val viewModelModule: Module = module {
    viewModel {
        SearchViewModel(
            interactor = get(),
            filterInteractor = get()
        )
    }
    viewModel {
        FavoritesViewModel(
            favoritesInteractor = get()
        )
    }
    viewModel {
        VacancyViewModel(
            vacancyInteractor = get(),
            favoritesInteractor = get(),
        )
    }
    viewModel {
        FilterViewModel(
            filterInteractor = get()
        )
    }
    viewModel {
        ChooseCountryViewModel(get())
    }
    viewModel {
        ChooseRegionViewModel(get())
    }
    viewModel {
        ChooseWorkPlaceViewModel(get(), get())
    }
    viewModel {
        ChooseIndustryViewModel(industryInteractor = get())
    }
}
