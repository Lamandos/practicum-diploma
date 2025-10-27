package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentChooseworkplaceBinding

class ChooseWorkPlaceFragment : Fragment(R.layout.fragment_chooseworkplace) {

    private var _binding: FragmentChooseworkplaceBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentChooseworkplaceBinding.bind(view)

        // Переход на FilterSettingsFragment
        binding.buttonToFilterSettings.setOnClickListener {
            findNavController().navigate(R.id.action_chooseWorkPlaceFragment_to_filterSettingsFragment)
        }

        // Переход на ChooseCountryFragment
        binding.buttonToChooseCountry.setOnClickListener {
            findNavController().navigate(R.id.action_chooseWorkPlaceFragment_to_chooseCountryFragment)
        }

        // Переход на ChooseRegionFragment
        binding.buttonToChooseRegion.setOnClickListener {
            findNavController().navigate(R.id.action_chooseWorkPlaceFragment_to_chooseRegionFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
