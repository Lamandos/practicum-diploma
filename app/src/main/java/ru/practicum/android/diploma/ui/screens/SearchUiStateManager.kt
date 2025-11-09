package ru.practicum.android.diploma.ui.screens

import android.content.Context
import android.view.View
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.domain.models.vacancy.Vacancy
import ru.practicum.android.diploma.presentation.search.adapter.SearchVacancyAdapter
import ru.practicum.android.diploma.presentation.search.viewmodel.SearchState
import ru.practicum.android.diploma.presentation.search.viewmodel.SearchViewModel

class SearchUiStateManager {

    companion object {
        private const val SERVER_ERROR_CODE = 500
    }

    fun handleSearchState(
        state: SearchState,
        binding: FragmentSearchBinding,
        adapter: SearchVacancyAdapter,
        viewModel: SearchViewModel,
        context: Context
    ) {
        when (state) {
            is SearchState.Idle -> handleIdleState(binding, adapter)
            is SearchState.Loading -> handleLoadingState(binding, adapter)
            is SearchState.Success -> handleSuccessState(state.vacancies, binding, adapter, viewModel, context)
            is SearchState.Empty -> handleEmptyState(binding, adapter)
            is SearchState.Error -> handleErrorState(state.throwable, binding, adapter, context)
        }
    }

    private fun handleIdleState(binding: FragmentSearchBinding, adapter: SearchVacancyAdapter) {
        updateUI(binding, emptyList(), isSearchActive = false)
        adapter.showLoading(false)
    }

    private fun handleLoadingState(binding: FragmentSearchBinding, adapter: SearchVacancyAdapter) {
        updateUI(binding, emptyList(), isSearchActive = true, isLoading = true)
        adapter.showLoading(false)
    }

    private fun handleSuccessState(
        vacancies: List<Vacancy>,
        binding: FragmentSearchBinding,
        adapter: SearchVacancyAdapter,
        viewModel: SearchViewModel,
        context: Context
    ) {
        updateUI(binding, vacancies, isSearchActive = true)
        showVacanciesState(vacancies, binding, adapter, viewModel, context)
    }

    private fun handleEmptyState(binding: FragmentSearchBinding, adapter: SearchVacancyAdapter) {
        updateUI(binding, emptyList(), isSearchActive = true)
        adapter.showLoading(false)
    }

    private fun handleErrorState(
        throwable: Throwable?,
        binding: FragmentSearchBinding,
        adapter: SearchVacancyAdapter,
        context: Context
    ) {
        val hasNetwork = isNetworkAvailable(context)
        val isServerError = checkServerError(throwable)
        val uiConfig = UiConfig(
            vacancies = emptyList(),
            isSearchActive = true,
            isLoading = false,
            isNetworkAvailable = hasNetwork,
            isServerError = isServerError
        )
        updateVisibilityStates(binding, uiConfig)
        adapter.showLoading(false)
    }

    private fun checkServerError(throwable: Throwable?): Boolean {
        return (throwable as? retrofit2.HttpException)?.code() == SERVER_ERROR_CODE
    }

    private fun updateUI(
        binding: FragmentSearchBinding,
        vacancies: List<Vacancy>,
        isSearchActive: Boolean,
        isLoading: Boolean = false
    ) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        val uiConfig = UiConfig(
            vacancies = vacancies,
            isSearchActive = isSearchActive,
            isLoading = isLoading,
            isNetworkAvailable = true,
            isServerError = false
        )
        updateVisibilityStates(binding, uiConfig)
    }

    private fun updateVisibilityStates(binding: FragmentSearchBinding, config: UiConfig) {
        when {
            config.isLoading -> showLoadingState(binding)
            !config.isNetworkAvailable -> showNoNetworkState(binding)
            config.isServerError -> showServerErrorState(binding)
            config.vacancies.isEmpty() && config.isSearchActive -> showNoVacanciesState(binding)
            config.vacancies.isNotEmpty() -> Unit // handled in handleSuccessState
            else -> showInitialState(binding)
        }
    }

    private fun showLoadingState(binding: FragmentSearchBinding) {
        setViewsVisibility(
            binding,
            VisibilityConfig(
                noNetError = View.GONE,
                serverError = View.GONE,
                noVacError = View.GONE,
                searchStartPic = View.GONE,
                recyclerView = View.GONE,
                msgText = View.GONE
            )
        )
    }

    private fun showNoNetworkState(binding: FragmentSearchBinding) {
        setViewsVisibility(
            binding,
            VisibilityConfig(
                noNetError = View.VISIBLE,
                serverError = View.GONE,
                noVacError = View.GONE,
                searchStartPic = View.GONE,
                recyclerView = View.GONE,
                msgText = View.GONE
            )
        )
    }

    private fun showServerErrorState(binding: FragmentSearchBinding) {
        setViewsVisibility(
            binding,
            VisibilityConfig(
                noNetError = View.GONE,
                serverError = View.VISIBLE,
                noVacError = View.GONE,
                searchStartPic = View.GONE,
                recyclerView = View.GONE,
                msgText = View.GONE
            )
        )
    }

    private fun showNoVacanciesState(binding: FragmentSearchBinding) {
        setViewsVisibility(
            binding,
            VisibilityConfig(
                noNetError = View.GONE,
                serverError = View.GONE,
                noVacError = View.VISIBLE,
                searchStartPic = View.GONE,
                recyclerView = View.GONE,
                msgText = View.VISIBLE
            )
        )
        binding.msgText.text = binding.root.context.getString(R.string.no_vac_msg)
    }

    private fun showVacanciesState(
        vacancies: List<Vacancy>,
        binding: FragmentSearchBinding,
        adapter: SearchVacancyAdapter,
        viewModel: SearchViewModel,
        context: Context
    ) {
        setViewsVisibility(
            binding,
            VisibilityConfig(
                noNetError = View.GONE,
                serverError = View.GONE,
                noVacError = View.GONE,
                searchStartPic = View.GONE,
                recyclerView = View.VISIBLE,
                msgText = View.VISIBLE
            )
        )
        binding.msgText.text = context.resources.getQuantityString(
            R.plurals.found_vac_msg,
            viewModel.totalFoundCount,
            viewModel.totalFoundCount
        )
        adapter.setItems(vacancies)
    }

    private fun showInitialState(binding: FragmentSearchBinding) {
        setViewsVisibility(
            binding,
            VisibilityConfig(
                noNetError = View.GONE,
                serverError = View.GONE,
                noVacError = View.GONE,
                searchStartPic = View.VISIBLE,
                recyclerView = View.GONE,
                msgText = View.GONE
            )
        )
    }

    private fun setViewsVisibility(binding: FragmentSearchBinding, config: VisibilityConfig) {
        binding.noNetError.visibility = config.noNetError
        binding.serverError.visibility = config.serverError
        binding.noVacError.visibility = config.noVacError
        binding.searchStartPic.visibility = config.searchStartPic
        binding.recyclerView.visibility = config.recyclerView
        binding.msgText.visibility = config.msgText
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as android.net.ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private data class UiConfig(
        val vacancies: List<Vacancy>,
        val isSearchActive: Boolean,
        val isLoading: Boolean,
        val isNetworkAvailable: Boolean,
        val isServerError: Boolean
    )

    private data class VisibilityConfig(
        val noNetError: Int,
        val serverError: Int,
        val noVacError: Int,
        val searchStartPic: Int,
        val recyclerView: Int,
        val msgText: Int
    )
}
