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
import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto
import ru.practicum.android.diploma.data.repositories.IndustriesRepository
import ru.practicum.android.diploma.databinding.FragmentChooseindustryBinding
import ru.practicum.android.diploma.presentation.filter.adapter.IndustryAdapter

class ChooseIndustryFragment : Fragment(R.layout.fragment_chooseindustry) {

    private var _binding: FragmentChooseindustryBinding? = null
    private val binding get() = _binding!!
    private val repository: IndustriesRepository by inject()

    private val adapter: IndustryAdapter by lazy {
        IndustryAdapter(emptyList()) { selectedIndustry ->
            // обработка выбора индустрии
        }
    }

    private var allIndustries: List<FilterIndustryDto> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChooseindustryBinding.bind(view)

        setupRecyclerView()
        setupClickListeners()
        setupSearchField()
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

        binding.clearIcon.setOnClickListener {
            binding.searchField.text?.clear()
        }
    }

    private fun setupSearchField() {
        binding.searchField.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString().trim()
                filterIndustries(query)
                binding.clearIcon.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                binding.searchIcon.visibility = if (query.isNotEmpty()) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(editable: android.text.Editable?) {
            }
        })
    }

    private fun filterIndustries(query: String) {
        val filtered = if (query.isEmpty()) {
            allIndustries
        } else {
            allIndustries.filter { it.name.contains(query, ignoreCase = true) }
        }
        adapter.updateData(filtered)
    }

    private fun loadIndustries() {
        lifecycleScope.launch {
            allIndustries = repository.getAllIndustries().orEmpty()
            adapter.updateData(allIndustries)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
