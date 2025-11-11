package ru.practicum.android.diploma.ui.screens.filter

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class FilterClearManager(
    private val uiStateManager: FilterUIStateManager,
    private val dataManager: FilterDataManager,
    private val salaryManager: SalaryFieldManager
) {

    fun clearAllFields(
        jobLocationEditText: TextInputEditText,
        jobLocationLayout: TextInputLayout,
        industryEditText: TextInputEditText,
        industryLayout: TextInputLayout,
        checkbox: com.google.android.material.checkbox.MaterialCheckBox
    ) {
        clearJobLocation(jobLocationEditText, jobLocationLayout)
        clearIndustry(industryEditText, industryLayout)
        clearSalary()
        clearCheckbox(checkbox)

        dataManager.clearDraftFilters()
    }

    fun clearJobLocation(
        editText: TextInputEditText,
        layout: TextInputLayout
    ) {
        editText.text?.clear()
        uiStateManager.updateIconAndState(layout, "")
        dataManager.selectedCountry = null
        dataManager.selectedRegion = null
    }

    fun clearIndustry(
        editText: TextInputEditText,
        layout: TextInputLayout
    ) {
        editText.text?.clear()
        uiStateManager.updateIconAndState(layout, "")
        dataManager.selectedIndustry = null
    }

    private fun clearSalary() {
        salaryManager.clearSalary()
    }

    private fun clearCheckbox(checkbox: com.google.android.material.checkbox.MaterialCheckBox) {
        checkbox.isChecked = false
    }
}
