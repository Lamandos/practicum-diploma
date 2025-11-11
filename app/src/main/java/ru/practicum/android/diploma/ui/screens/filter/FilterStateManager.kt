package ru.practicum.android.diploma.ui.screens.filter

import android.util.Log
import ru.practicum.android.diploma.data.storage.FilterPreferences
import ru.practicum.android.diploma.domain.models.filtermodels.Industry
import ru.practicum.android.diploma.domain.models.filtermodels.Region
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters
import ru.practicum.android.diploma.domain.models.filtermodels.isAnyFilterApplied
import ru.practicum.android.diploma.domain.models.vacancy.Country
import ru.practicum.android.diploma.presentation.filter.viewmodel.FilterViewModel
import ru.practicum.android.diploma.ui.model.FilterIndustryUI
import ru.practicum.android.diploma.ui.screens.FilterSettingsFragment

class FilterStateManager(
    private val fragment: FilterSettingsFragment,
    private val viewModel: FilterViewModel,
    private val filterPreferences: FilterPreferences
) {
    private val binding get() = fragment.binding

    var hasUnsavedChanges = false
    var isFilterApplied = false

    companion object {
        private const val FILTER_DRAFT_TAG = "FilterDraft"
        private const val CURRENCY_RUB = "RUB"
    }

    fun createFiltersFromUI(): VacancyFilters {
        val industry = fragment.selectedIndustry?.let { industryUI ->
            Industry(
                id = industryUI.id.toString(),
                name = industryUI.name,
                parentId = null
            )
        }

        val salary = binding.editSalary.text?.toString()?.toIntOrNull()
        val hideWithoutSalary = binding.checkbox.isChecked

        val regionToSave = when {
            fragment.selectedRegion != null -> fragment.selectedRegion
            fragment.selectedCountry != null -> Region(
                id = fragment.selectedCountry!!.id,
                name = "",
                country = fragment.selectedCountry
            )
            else -> null
        }

        return VacancyFilters(
            region = regionToSave,
            industry = industry,
            salary = salary,
            hideWithoutSalary = hideWithoutSalary,
            currency = CURRENCY_RUB
        )
    }

    fun saveDraftFilters() {
        if (!fragment.isViewCreated) return

        hasUnsavedChanges = true
        val draftFilters = createFiltersFromUI()

        Log.d(FILTER_DRAFT_TAG, "Saving draft filters: $draftFilters")
        Log.d(FILTER_DRAFT_TAG, "Selected country: ${fragment.selectedCountry}")
        Log.d(FILTER_DRAFT_TAG, "Selected region: ${fragment.selectedRegion}")

        filterPreferences.saveDraftFilters(draftFilters)
    }

    fun loadDraftFilters() {
        val draftFilters = filterPreferences.getDraftFilters()

        Log.d(FILTER_DRAFT_TAG, "Loading draft filters: $draftFilters")
        Log.d(FILTER_DRAFT_TAG, "Checkbox state in draft: ${draftFilters.hideWithoutSalary}")

        if (draftFilters.isAnyFilterApplied()) {
            updateUIWithDraftFilters(draftFilters)
            hasUnsavedChanges = true
        } else {
            fragment.loadSavedFilters()
        }
    }

    private fun updateUIWithDraftFilters(filters: VacancyFilters) {
        // Обновляем отрасль из черновиков
        filters.industry?.let { industry ->
            binding.editIndustry.setText(industry.name)
            fragment.selectedIndustry = FilterIndustryUI(
                id = industry.id.toIntOrNull() ?: 0,
                name = industry.name
            )
            fragment.updateIconAndState(binding.industry, industry.name)
        }

        // Обновляем зарплату из черновиков
        filters.salary?.let { salary ->
            binding.editSalary.setText(salary.toString())
        }

        // Обновляем чекбокс из черновиков
        binding.checkbox.isChecked = filters.hideWithoutSalary ?: false

        // Обновляем регион из черновиков
        filters.region?.let { region ->
            fragment.selectedRegion = if (region.name.isNotEmpty()) region else null
            fragment.selectedCountry = region.country

            val locationText = when {
                region.country != null && region.name.isNotBlank() ->
                    "${region.country.name}, ${region.name}"
                region.country != null -> region.country.name
                else -> region.name
            }

            binding.editJobLocation.setText(locationText)
            fragment.updateIconAndState(binding.jobLocation, locationText)
        }

        fragment.updateButtonsVisibility()
    }

    fun applyFiltersAndReturn() {
        filterPreferences.applyDraftFilters()
        isFilterApplied = true
    }

    fun clearAllFilters() {
        fragment.selectedIndustry = null
        fragment.selectedCountry = null
        fragment.selectedRegion = null
        hasUnsavedChanges = false
        isFilterApplied = false

        filterPreferences.clearDraftFilters()
        viewModel.clearFilters()
    }

    fun hasAnyFilterApplied(): Boolean {
        return binding.editJobLocation.text?.isNotBlank() == true ||
            binding.editIndustry.text?.isNotBlank() == true ||
            binding.editSalary.text?.isNotBlank() == true ||
            binding.checkbox.isChecked
    }
}
