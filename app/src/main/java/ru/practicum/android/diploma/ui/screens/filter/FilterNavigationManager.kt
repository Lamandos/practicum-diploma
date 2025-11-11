package ru.practicum.android.diploma.ui.screens.filter

import androidx.navigation.NavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.practicum.android.diploma.R

class FilterNavigationManager(
    private val navController: NavController,
    private val clearManager: FilterClearManager
) {

    fun navigateOrClearField(
        editText: TextInputEditText,
        layout: TextInputLayout,
        fieldType: FieldType
    ) {
        if (editText.text.isNullOrEmpty()) {
            navigateToFieldSelection(fieldType)
        } else {
            clearField(editText, layout, fieldType)
        }
    }

    private fun navigateToFieldSelection(fieldType: FieldType) {
        when (fieldType) {
            FieldType.JOB_LOCATION -> navController.navigate(
                R.id.action_filterSettingsFragment_to_chooseWorkPlaceFragment
            )
            FieldType.INDUSTRY -> navController.navigate(
                R.id.action_filterSettingsFragment_to_chooseIndustryFragment
            )
        }
    }

    private fun clearField(
        editText: TextInputEditText,
        layout: TextInputLayout,
        fieldType: FieldType
    ) {
        editText.text?.clear()
        when (fieldType) {
            FieldType.INDUSTRY -> clearManager.clearIndustry(editText, layout)
            FieldType.JOB_LOCATION -> clearManager.clearJobLocation(editText, layout)
        }
    }

    enum class FieldType {
        JOB_LOCATION,
        INDUSTRY
    }
}
