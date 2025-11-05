// VacancyFragment.kt
package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.models.vacancy.Salary
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import ru.practicum.android.diploma.presentation.details.VacancyViewModel
import ru.practicum.android.diploma.util.formatsalary.formatSalary

class VacancyFragment : Fragment(R.layout.fragment_vacancy) {

    private var _binding: FragmentVacancyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VacancyViewModel by viewModel()
    private var vacancyId: String? = null
    private var fromFavorites: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vacancyId = arguments?.getString("vacancyId")
        fromFavorites = arguments?.getBoolean("fromFavorites", false) ?: false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVacancyBinding.bind(view)
        setupClickListeners()
        setupObservers()
        updateFavoritesButton(false)

        vacancyId?.let { id ->
            viewModel.init(id, fromFavorites)
        } ?: run {
            findNavController().popBackStack()
        }
    }

    private fun setupClickListeners() {
        binding.favoritesBtn.setOnClickListener {
            viewModel.onFavoritesClicked()
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupObservers() {
        viewModel.vacancyDetails.observe(viewLifecycleOwner) { vacancy ->
            if (vacancy != null) {
                showVacancy(vacancy)
            } else {
                if (fromFavorites) {
                    findNavController().popBackStack()
                }
            }
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            updateFavoritesButton(isFavorite)
            if (fromFavorites && !isFavorite) {
                findNavController().popBackStack()
                return@observe
            }
        }
    }

    private fun showVacancy(vacancy: VacancyDetails) {
        binding.vacName.text = vacancy.name
        binding.vacEmployer.text = vacancy.employer
        binding.vacSalary.text = formatSalary(vacancy.salary as Salary?)
    }

    private fun updateFavoritesButton(isFavorite: Boolean) {
        if (isFavorite) {
            binding.favoritesBtn.setImageResource(R.drawable.favorite_fill_icon)
        } else {
            binding.favoritesBtn.setImageResource(R.drawable.favorite_icon)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
