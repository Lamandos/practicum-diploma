package ru.practicum.android.diploma.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import ru.practicum.android.diploma.presentation.details.VacancyViewModel

class VacancyFragment : Fragment(R.layout.fragment_vacancy) {

    private var _binding: FragmentVacancyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VacancyViewModel by viewModel()
    private var vacancyId: String? = null
    private var fromFavorites: Boolean = false

    private val vacancyBinder = VacancyBinder()

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
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.favoritesBtn.setOnClickListener {
            viewModel.onFavoritesClicked()
        }
        binding.shareBtn.setOnClickListener {
            viewModel.vacancyDetails.value?.let { shareVacancy(it.url) }
        }
    }

    private fun shareVacancy(url: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_vacancy_title)))
    }

    private fun setupObservers() {
        viewModel.vacancyDetails.observe(viewLifecycleOwner) { vacancy ->
            handleVacancyDetails(vacancy)
        }
        viewModel.error.observe(viewLifecycleOwner) { errorCode ->
            handleErrorState(errorCode)
        }
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            updateFavoritesButton(isFavorite)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun handleVacancyDetails(vacancy: VacancyDetails?) {
        if (vacancy != null) {
            binding.fullVacInfo.visibility = View.VISIBLE
            vacancyBinder.bindVacancyDetails(binding, vacancy, requireContext())
            binding.vacDelError.visibility = View.GONE
            binding.serverError.visibility = View.GONE
        }
    }

    private fun handleErrorState(errorCode: String?) {
        binding.fullVacInfo.visibility = View.GONE
        when (errorCode) {
            VacancyViewModel.ERROR_VACANCY_NOT_FOUND -> {
                binding.vacDelError.visibility = View.VISIBLE
                binding.serverError.visibility = View.GONE
            }
            VacancyViewModel.ERROR_SERVER -> {
                binding.serverError.visibility = View.VISIBLE
                binding.vacDelError.visibility = View.GONE
            }
        }
    }

    private fun updateFavoritesButton(isFavorite: Boolean) {
        val drawable = if (isFavorite) {
            AppCompatResources.getDrawable(requireContext(), R.drawable.favorite_fill_icon)
        } else {
            AppCompatResources.getDrawable(requireContext(), R.drawable.favorite_icon)
        }
        binding.favoritesBtn.setImageDrawable(drawable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
