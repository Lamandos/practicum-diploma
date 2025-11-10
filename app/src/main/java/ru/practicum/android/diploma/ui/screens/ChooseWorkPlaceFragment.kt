package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentChooseworkplaceBinding
import ru.practicum.android.diploma.domain.models.filtermodels.Region
import ru.practicum.android.diploma.domain.models.vacancy.Country
import ru.practicum.android.diploma.presentation.filter.viewmodel.ChooseWorkPlaceViewModel

class ChooseWorkPlaceFragment : Fragment(R.layout.fragment_chooseworkplace) {

    private var _binding: FragmentChooseworkplaceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChooseWorkPlaceViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChooseworkplaceBinding.bind(view)

        binding.country.endIconMode = TextInputLayout.END_ICON_CUSTOM
        binding.region.endIconMode = TextInputLayout.END_ICON_CUSTOM

        setupClickListeners()
        setupTextFields()
        observeViewModel()

        updateIconAndState(binding.country, binding.editCountry.text?.toString().orEmpty())
        updateIconAndState(binding.region, binding.editRegion.text?.toString().orEmpty())

        viewModel.loadCountries()
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener { findNavController().popBackStack() }
        binding.country.setEndIconOnClickListener {
            val selected = viewModel.selectedCountry.value
            if (selected == null) {
                findNavController().navigate(R.id.action_chooseWorkPlaceFragment_to_chooseCountryFragment)
            } else {
                binding.editCountry.text?.clear()
                viewModel.selectCountry(null)
                binding.editRegion.text?.clear()
                viewModel.selectRegion(null)
                updateIconAndState(binding.country, "")
                updateIconAndState(binding.region, "")
            }
        }

        binding.region.setEndIconOnClickListener {
            val selected = viewModel.selectedRegion.value
            if (selected == null) {
                val country = viewModel.selectedCountry.value
                val bundle = Bundle().apply {
                    country?.let { putParcelable("country", it) }
                }
                findNavController().navigate(R.id.action_chooseWorkPlaceFragment_to_chooseRegionFragment, bundle)
            } else {
                binding.editRegion.text?.clear()
                viewModel.selectRegion(null)
                updateIconAndState(binding.region, "")
            }
        }
        binding.btnAccept.setOnClickListener {
            val result = Bundle().apply {
                viewModel.selectedCountry.value?.let { putParcelable("country", it) }
                viewModel.selectedRegion.value?.let { putParcelable("region", it) }
            }

            parentFragmentManager.setFragmentResult("workplace_result", result)
            findNavController().popBackStack()
        }
    }

    private fun setupTextFields() {
        binding.editCountry.addTextChangedListener {
            updateIconAndState(binding.country, it?.toString().orEmpty())
        }
        binding.editRegion.addTextChangedListener {
            updateIconAndState(binding.region, it?.toString().orEmpty())
        }
    }

    private fun updateIconAndState(layout: TextInputLayout, text: String) {
        if (text.isEmpty()) {
            layout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.arrow_right)
        } else {
            layout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.clear_icon)
        }
        layout.isActivated = text.isNotEmpty()
        layout.isSelected = text.isNotEmpty()
    }

    private fun observeViewModel() {
        parentFragmentManager.setFragmentResultListener("country_request", viewLifecycleOwner) { _, bundle ->
            bundle.getParcelable<Country>("country")?.let { country ->
                viewModel.selectCountry(country)
                binding.editCountry.setText(country.name)
                binding.editCountry.tag = country
                binding.editRegion.text?.clear()
                viewModel.selectRegion(null)
                updateIconAndState(binding.region, "")
                updateIconAndState(binding.country, country.name)
            }
        }

        parentFragmentManager.setFragmentResultListener("region_request", viewLifecycleOwner) { _, bundle ->
            bundle.getParcelable<Region>("region")?.let { region ->
                if (viewModel.selectedCountry.value == null && region.country != null) {
                    viewModel.selectCountry(region.country)
                    binding.editCountry.setText(region.country.name)
                    binding.editCountry.tag = region.country
                    updateIconAndState(binding.country, region.country.name)
                }

                viewModel.selectRegion(region)
                binding.editRegion.setText(region.name)
                updateIconAndState(binding.region, region.name)
            }
        }

        viewModel.selectedCountry.observe(viewLifecycleOwner) { updateAcceptButtonVisibility() }
        viewModel.selectedRegion.observe(viewLifecycleOwner) { updateAcceptButtonVisibility() }
    }

    private fun updateAcceptButtonVisibility() {
        binding.btnAccept.visibility =
            if (viewModel.selectedCountry.value != null || viewModel.selectedRegion.value != null) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
