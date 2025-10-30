package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.util.debounce.DebounceFactory

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    // Используем DebounceFactory для создания экземпляров
    private val clickDebounce = DebounceFactory.createClickDebounce()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchBinding.bind(view)

        //setupNavigationWithDebounce()
    }

//    private fun setupNavigationWithDebounce() {
//        // Все переходы защищены от множественных кликов
//
//        // Переход на VacancyFragment
//        binding.buttonToVacancy.setOnClickListener {
//            clickDebounce.submit {
//                findNavController().navigate(R.id.action_searchFragment_to_vacancyFragment2)
//            }
//        }
//
//        // Переход на FavouritesFragment
//        binding.buttonToFavourites.setOnClickListener {
//            clickDebounce.submit {
//                findNavController().navigate(R.id.action_searchFragment_to_favouritesFragment)
//            }
//        }
//
//        // Переход на TeamFragment
//        binding.buttonToTeam.setOnClickListener {
//            clickDebounce.submit {
//                findNavController().navigate(R.id.action_searchFragment_to_teamFragment)
//            }
//        }
//
//        // Переход на FilterSettingsFragment
//        binding.buttonToFilterSettings.setOnClickListener {
//            clickDebounce.submit {
//                findNavController().navigate(R.id.action_searchFragment_to_filterSettingsFragment)
//            }
//        }
//    }

    override fun onPause() {
        super.onPause()
        clickDebounce.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clickDebounce.cancel()
        _binding = null
    }
}
