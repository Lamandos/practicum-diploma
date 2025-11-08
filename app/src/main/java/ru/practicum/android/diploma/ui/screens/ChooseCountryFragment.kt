package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.repositories.AreasRepository
import ru.practicum.android.diploma.databinding.FragmentChoosecountryBinding
import ru.practicum.android.diploma.presentation.filter.adapter.CountryAdapter

class ChooseCountryFragment : Fragment(R.layout.fragment_choosecountry) {

    private var _binding: FragmentChoosecountryBinding? = null
    private val binding get() = _binding!!

    private val repository: AreasRepository by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChoosecountryBinding.bind(view)
        setupClickListeners()
        loadCountries()
    }
    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
    }

    private fun loadCountries() {
        lifecycleScope.launch {
            val allAreas = repository.getAllAreas()
            val countries = allAreas?.filter { it.parentId == null || it.parentId == 0 }.orEmpty()

            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = CountryAdapter(countries) { selected ->
                    // обработка клика по стране
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
