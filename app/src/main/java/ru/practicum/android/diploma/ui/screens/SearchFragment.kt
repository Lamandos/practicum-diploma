package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchBinding.bind(view)

        // Переход на VacancyFragment
        binding.buttonToVacancy.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_vacancyFragment2)
        }

        // Переход на FavouritesFragment
        binding.buttonToFavourites.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_favouritesFragment)
        }

        // Переход на TeamFragment
        binding.buttonToTeam.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_teamFragment)
        }

        // Переход на FilterSettingsFragment
        binding.buttonToFilterSettings.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_filterSettingsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
