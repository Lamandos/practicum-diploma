package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentChooseworkplaceBinding

class ChooseWorkPlaceFragment : Fragment(R.layout.fragment_chooseworkplace) {

    private var _binding: FragmentChooseworkplaceBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChooseworkplaceBinding.bind(view)
        setupClickListeners()

        val countryLayout: TextInputLayout = binding.country
        val countryEditText: TextInputEditText = binding.editCountry
        val regionLayout: TextInputLayout = binding.region
        val regionEditText: TextInputEditText = binding.editRegion

        countryLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
        regionLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM

        countryEditText.addTextChangedListener {
            updateIconAndState(countryLayout, it?.toString().orEmpty())
        }

        regionEditText.addTextChangedListener {
            updateIconAndState(regionLayout, it?.toString().orEmpty())
        }

        countryLayout.setEndIconOnClickListener {
            navigateOrClear(countryEditText, countryLayout, "country")
        }

        regionLayout.setEndIconOnClickListener {
            navigateOrClear(regionEditText, regionLayout, "region")
        }

        updateIconAndState(countryLayout, countryEditText.text.toString())

        updateIconAndState(regionLayout, regionEditText.text.toString())

        setupCountryListener()
        setupRegionListener()
    }
    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun updateIconAndState(layout: TextInputLayout, text: String) {
        if (text.isEmpty()) {
            layout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.arrow_right)
            layout.isSelected = false
            layout.isActivated = false
        } else {
            layout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.clear_icon)
            layout.isSelected = true
            layout.isActivated = true
        }
    }

    private fun navigateOrClear(
        editText: TextInputEditText,
        layout: TextInputLayout,
        field: String
    ) {
        if (!editText.text.isNullOrEmpty()) {
            editText.text?.clear()
            updateIconAndState(layout, "")

            if (field == "country") {
                binding.editRegion.text?.clear()
                updateIconAndState(binding.region, "")
                binding.editCountry.tag = null
            }

            return
        }

        when (field) {
            "country" -> {
                findNavController().navigate(
                    R.id.action_chooseWorkPlaceFragment_to_chooseCountryFragment
                )
            }

            "region" -> {
                val countryId = binding.editCountry.tag as? Int ?: -1
                val bundle = Bundle().apply {
                    putInt("country_id", countryId)
                }
                findNavController().navigate(
                    R.id.action_chooseWorkPlaceFragment_to_chooseRegionFragment,
                    bundle
                )
            }
        }
    }

    private fun setupCountryListener() {
        parentFragmentManager.setFragmentResultListener(
            "country_request",
            viewLifecycleOwner
        ) { _, bundle ->

            val name = bundle.getString("country_name")
            val id = bundle.getInt("country_id")

            binding.editCountry.setText(name)
            binding.editCountry.tag = id
            binding.editRegion.text?.clear()
            updateIconAndState(binding.region, "")

            updateIconAndState(binding.country, name.orEmpty())
        }
    }

    private fun setupRegionListener() {
        parentFragmentManager.setFragmentResultListener(
            "region_request",
            viewLifecycleOwner
        ) { _, bundle ->

            val name = bundle.getString("region_name")

            binding.editRegion.setText(name)
            updateIconAndState(binding.region, name.orEmpty())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
