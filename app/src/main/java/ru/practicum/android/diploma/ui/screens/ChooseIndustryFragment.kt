package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentChooseindustryBinding
import ru.practicum.android.diploma.presentation.filter.adapter.IndustryAdapter
import ru.practicum.android.diploma.presentation.filter.viewmodel.ChooseIndustryViewModel
import ru.practicum.android.diploma.ui.model.FilterIndustryUI

class ChooseIndustryFragment : Fragment(R.layout.fragment_chooseindustry) {

    private var _binding: FragmentChooseindustryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChooseIndustryViewModel by viewModel()

    private val adapter: IndustryAdapter by lazy {
        IndustryAdapter(emptyList()) { selectedIndustry ->
            onIndustrySelected(selectedIndustry)
        }
    }

    private var selectedIndustry: FilterIndustryUI? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChooseindustryBinding.bind(view)

        setupRecyclerView()
        setupClickListeners()
        setupSearchField()
        observeViewModel()
        updateButtonVisibility()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener { findNavController().popBackStack() }

        binding.clearIcon.setOnClickListener {
            binding.searchField.text?.clear()
        }

        binding.btnAccept.setOnClickListener { saveSelectedIndustryAndReturn() }
    }

    private fun setupSearchField() {
        binding.searchField.addTextChangedListener(object : android.text.TextWatcher {
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString().trim()
                viewModel.searchIndustries(query)
                binding.clearIcon.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                binding.searchIcon.visibility = if (query.isNotEmpty()) View.GONE else View.VISIBLE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: android.text.Editable?) = Unit
        })
    }

    private fun observeViewModel() {
        viewModel.industriesState.observe(viewLifecycleOwner) { industries ->
            adapter.updateData(industries)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            when (error) {
                ChooseIndustryViewModel.IndustryError.NoNetwork -> {
                    binding.noNetError.visibility = View.VISIBLE
                    binding.serverError.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                }
                ChooseIndustryViewModel.IndustryError.ServerError -> {
                    binding.serverError.visibility = View.VISIBLE
                    binding.noNetError.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                }
                null -> {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.noNetError.visibility = View.GONE
                    binding.serverError.visibility = View.GONE
                }
            }
        }
    }

    private fun onIndustrySelected(industry: FilterIndustryUI) {
        selectedIndustry = industry
        updateButtonVisibility()
    }

    private fun updateButtonVisibility() {
        binding.btnAccept.visibility = if (selectedIndustry != null) View.VISIBLE else View.GONE
    }

    private fun saveSelectedIndustryAndReturn() {
        selectedIndustry?.let { industry ->
            setFragmentResult(
                "industry_result",
                Bundle().apply { putParcelable("selected_industry", industry) }
            )
        }
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
