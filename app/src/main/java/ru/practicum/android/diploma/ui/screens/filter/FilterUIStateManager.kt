package ru.practicum.android.diploma.ui.screens.filter

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSettingsBinding

class FilterUIStateManager(
    private val binding: FragmentFilterSettingsBinding,
    private val context: Context
) {

    fun updateButtonsVisibility(hasFilters: Boolean) {
        binding.btnAccept.visibility = if (hasFilters) View.VISIBLE else View.GONE
        binding.btnDeny.visibility = if (hasFilters) View.VISIBLE else View.GONE
    }

    fun hasAnyFilterApplied(): Boolean {
        return binding.editJobLocation.text?.isNotBlank() == true ||
            binding.editIndustry.text?.isNotBlank() == true ||
            binding.editSalary.text?.isNotBlank() == true ||
            binding.checkbox.isChecked
    }

    fun updateIconAndState(layout: TextInputLayout, text: String) {
        if (text.isEmpty()) {
            layout.endIconDrawable = ContextCompat.getDrawable(
                context,
                R.drawable.arrow_right
            )
            layout.isSelected = false
            layout.isActivated = false
        } else {
            layout.endIconDrawable = ContextCompat.getDrawable(
                context,
                R.drawable.clear_icon
            )
            layout.isSelected = true
            layout.isActivated = true
        }
    }
}
