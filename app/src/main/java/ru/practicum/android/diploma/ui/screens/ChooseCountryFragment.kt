package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentChoosecountryBinding

class ChooseCountryFragment : Fragment(R.layout.fragment_choosecountry) {

    private var _binding: FragmentChoosecountryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentChoosecountryBinding.bind(view)

        // Переход на ChooseWorkPlaceFragment
        binding.buttonToChooseWorkPlace.setOnClickListener {
            findNavController().navigate(R.id.action_chooseCountryFragment_to_chooseWorkPlaceFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
