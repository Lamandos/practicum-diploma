package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.repositories.IndustriesRepository
import ru.practicum.android.diploma.databinding.FragmentChooseindustryBinding
import ru.practicum.android.diploma.presentation.filter.adapter.IndustryAdapter

class ChooseIndustryFragment : Fragment(R.layout.fragment_chooseindustry) {

    private var _binding: FragmentChooseindustryBinding? = null
    private val binding get() = _binding!!
    private val repository: IndustriesRepository by inject()

    private val adapter: IndustryAdapter by lazy {
        IndustryAdapter(emptyList()) { selectedIndustry ->
            // обработка клика по индустрии
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChooseindustryBinding.bind(view)

        setupRecyclerView()
        setupClickListeners()
        loadIndustries()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun loadIndustries() {
        lifecycleScope.launch {
            val industries = repository.getAllIndustries().orEmpty()
            adapter.updateData(industries)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
