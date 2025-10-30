package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSettingsBinding

class FilterSettingsFragment : Fragment(R.layout.fragment_filter_settings) {

    private var _binding: FragmentFilterSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFilterSettingsBinding.bind(view)

        // Переход на ChooseWorkPlaceFragment
        binding.buttonToChooseWorkPlace.setOnClickListener {
            findNavController().navigate(R.id.action_filterSettingsFragment_to_chooseWorkPlaceFragment)
        }

        // Переход на ChooseIndustryFragment
        binding.buttonToChooseIndustry.setOnClickListener {
            findNavController().navigate(R.id.action_filterSettingsFragment_to_chooseIndustryFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
