package ru.practicum.android.diploma.ui.screens.filter

import ru.practicum.android.diploma.data.storage.FilterPreferences
import ru.practicum.android.diploma.domain.models.filtermodels.Industry
import ru.practicum.android.diploma.domain.models.filtermodels.Region
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters
import ru.practicum.android.diploma.domain.models.vacancy.Country
import ru.practicum.android.diploma.ui.model.FilterIndustryUI

class FilterDataManager(
    private val filterPreferences: FilterPreferences
) {
    var selectedIndustry: FilterIndustryUI? = null
    var selectedCountry: Country? = null
    var selectedRegion: Region? = null

    fun createFiltersFromUI(
        salaryText: String?,
        hideWithoutSalary: Boolean
    ): VacancyFilters {
        val industry = selectedIndustry?.let { industryUI ->
            Industry(
                id = industryUI.id.toString(),
                name = industryUI.name,
                parentId = null
            )
        }

        val salary = salaryText?.toIntOrNull()

        val regionToSave = when {
            selectedRegion != null -> selectedRegion
            selectedCountry != null -> Region(
                id = selectedCountry!!.id,
                name = "",
                country = selectedCountry
            )
            else -> null
        }

        return VacancyFilters(
            region = regionToSave,
            industry = industry,
            salary = salary,
            hideWithoutSalary = hideWithoutSalary,
            currency = "RUB"
        )
    }

    fun saveDraftFilters(salaryText: String?, hideWithoutSalary: Boolean) {
        val draftFilters = createFiltersFromUI(salaryText, hideWithoutSalary)
        filterPreferences.saveDraftFilters(draftFilters)
    }

    fun loadDraftFilters(): VacancyFilters {
        return filterPreferences.getDraftFilters()
    }

    fun clearDraftFilters() {
        filterPreferences.clearDraftFilters()
        selectedIndustry = null
        selectedCountry = null
        selectedRegion = null
    }

    fun applyDraftFilters() {
        filterPreferences.applyDraftFilters()
    }
}
