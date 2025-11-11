package ru.practicum.android.diploma.ui.screens.filter

import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.ui.screens.FilterSettingsFragment

class FilterUIManager(private val fragment: FilterSettingsFragment) {
    private val binding get() = fragment.binding

    fun updateButtonsVisibility() {
        val hasFilters = fragment.hasAnyFilterApplied()
        binding.btnAccept.visibility = if (hasFilters) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnDeny.visibility = if (hasFilters) android.view.View.VISIBLE else android.view.View.GONE
        fragment.updateFilterAppliedState()
    }

    fun updateIconAndState(layout: TextInputLayout, text: String) {
        if (text.isEmpty()) {
            layout.endIconDrawable = ContextCompat.getDrawable(
                fragment.requireContext(),
                R.drawable.arrow_right
            )
            layout.isSelected = false
            layout.isActivated = false
        } else {
            layout.endIconDrawable = ContextCompat.getDrawable(
                fragment.requireContext(),
                R.drawable.clear_icon
            )
            layout.isSelected = true
            layout.isActivated = true
        }
    }

    fun clearAllFields() {
        binding.editJobLocation.text?.clear()
        fragment.selectedCountry = null
        fragment.selectedRegion = null
        updateIconAndState(binding.jobLocation, "")

        binding.editIndustry.text?.clear()
        updateIconAndState(binding.industry, "")
        fragment.selectedIndustry = null

        binding.editSalary.text?.clear()
        binding.clearIcon.visibility = android.view.View.GONE

        binding.checkbox.isChecked = false

        updateButtonsVisibility()
    }
}
