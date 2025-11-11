package ru.practicum.android.diploma.ui.screens.filter

import android.text.InputFilter
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.ui.screens.FilterSettingsFragment

class FilterFieldHandler(private val fragment: FilterSettingsFragment) {
    private val binding get() = fragment.binding
    private val context get() = fragment.requireContext()

    private val jobLocationField = "jobLocation"
    private val industryField = "industry"

    fun setupTextWatchers() {
        val jobLocationLayout: TextInputLayout = binding.jobLocation
        val jobLocationEditText: TextInputEditText = binding.editJobLocation
        val industryLayout: TextInputLayout = binding.industry
        val industryEditText: TextInputEditText = binding.editIndustry

        jobLocationLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
        industryLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM

        jobLocationEditText.addTextChangedListener {
            updateIconAndState(jobLocationLayout, it?.toString().orEmpty())
            fragment.updateButtonsVisibility()
            fragment.saveDraftFilters()
        }

        industryEditText.addTextChangedListener {
            updateIconAndState(industryLayout, it?.toString().orEmpty())
            fragment.updateButtonsVisibility()
            fragment.saveDraftFilters()
        }

        jobLocationLayout.setEndIconOnClickListener {
            navigateOrClear(jobLocationEditText, jobLocationLayout, jobLocationField)
        }

        industryLayout.setEndIconOnClickListener {
            navigateOrClear(industryEditText, industryLayout, industryField)
        }

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            fragment.updateButtonsVisibility()
            fragment.saveDraftFilters()
            fragment.updateFilterAppliedState()
        }

        updateIconAndState(jobLocationLayout, jobLocationEditText.text.toString())
        updateIconAndState(industryLayout, industryEditText.text.toString())

        setupSalaryField(binding.editSalary, binding.salaryField)
    }

    private fun setupSalaryField(editText: TextInputEditText, layout: TextInputLayout) {
        setupSalaryInputFilter(editText)
        setupSalaryTextWatcher(editText)
        setupSalaryFocusBehavior(editText)
        setupSalaryEditorDoneAction(editText, layout)
        setupSalaryClearIcon(editText)
    }

    private fun setupSalaryInputFilter(editText: TextInputEditText) {
        editText.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[0-9]+"))) source else ""
        })
    }

    private fun setupSalaryTextWatcher(editText: TextInputEditText) {
        editText.addTextChangedListener(
            object : android.text.TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                    if (s.toString() == "0") {
                        binding.clearIcon.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(s: android.text.Editable?) {
                    fragment.updateButtonsVisibility()
                    fragment.saveDraftFilters()
                }
            }
        )
    }

    private fun setupSalaryFocusBehavior(editText: TextInputEditText) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !editText.text.isNullOrEmpty()) {
                binding.clearIcon.visibility = View.VISIBLE
            }
        }
    }

    private fun setupSalaryEditorDoneAction(
        editText: TextInputEditText,
        layout: TextInputLayout,
    ) {
        editText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.clearIcon.visibility = View.GONE
                closeKeyboard(editText)

                editText.clearFocus()
                layout.clearFocus()

                binding.root.isFocusable = true
                binding.root.isFocusableInTouchMode = true
                binding.root.requestFocus()
                true
            } else {
                false
            }
        }
    }

    private fun setupSalaryClearIcon(editText: TextInputEditText) {
        binding.clearIcon.setOnClickListener {
            editText.text?.clear()
            fragment.saveDraftFilters()
        }
    }

    private fun updateIconAndState(layout: TextInputLayout, text: String) {
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

    private fun navigateOrClear(
        editText: TextInputEditText,
        layout: TextInputLayout,
        field: String,
    ) {
        if (editText.text.isNullOrEmpty()) {
            when (field) {
                jobLocationField -> {
                    fragment.findNavController().navigate(
                        ru.practicum.android.diploma.R.id.action_filterSettingsFragment_to_chooseWorkPlaceFragment
                    )
                }

                industryField -> fragment.findNavController().navigate(
                    ru.practicum.android.diploma.R.id.action_filterSettingsFragment_to_chooseIndustryFragment
                )
            }
        } else {
            editText.text?.clear()
            updateIconAndState(layout, "")
            when (field) {
                industryField -> {
                    fragment.selectedIndustry = null
                    fragment.saveDraftFilters()
                }

                jobLocationField -> {
                    fragment.selectedCountry = null
                    fragment.selectedRegion = null
                    fragment.saveDraftFilters()
                }
            }
            fragment.updateButtonsVisibility()
        }
    }

    private fun closeKeyboard(view: android.view.View) {
        val imm = context.getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
