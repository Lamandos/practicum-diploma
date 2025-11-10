package ru.practicum.android.diploma.presentation.mappers

import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.ui.model.FilterIndustryUI

object IndustryUiMapper {

    fun mapDomainToUi(domain: FilterIndustry): FilterIndustryUI {
        return FilterIndustryUI(
            id = domain.id,
            name = domain.name
        )
    }

    fun mapDomainListToUi(domainList: List<FilterIndustry>): List<FilterIndustryUI> {
        return domainList.map { mapDomainToUi(it) }
    }
}
