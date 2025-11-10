package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto
import ru.practicum.android.diploma.databinding.FragmentChooseindustryBinding
import ru.practicum.android.diploma.domain.api.repositories.IndustryRepository
import ru.practicum.android.diploma.presentation.filter.adapter.IndustryAdapter
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ChooseIndustryFragment : Fragment(R.layout.fragment_chooseindustry) {

    private var _binding: FragmentChooseindustryBinding? = null
    private val binding get() = _binding!!
    private val repository: IndustryRepository by inject()

    private val adapter: IndustryAdapter by lazy {
        IndustryAdapter(emptyList()) { selectedIndustry ->
            onIndustrySelected(selectedIndustry)
        }
    }

    private var allIndustries: List<FilterIndustryDto> = emptyList()
    private var selectedIndustry: FilterIndustryDto? = null

    companion object {
        private const val TAG = "ChooseIndustryFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChooseindustryBinding.bind(view)

        setupRecyclerView()
        setupClickListeners()
        setupSearchField()
        loadIndustries()
        updateButtonVisibility()
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

        binding.btnAccept.setOnClickListener {
            saveSelectedIndustryAndReturn()
        }
    }

    private fun setupSearchField() {
        binding.searchField.addTextChangedListener(object : android.text.TextWatcher {
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString().trim()
                filterIndustries(query)
                binding.clearIcon.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                binding.searchIcon.visibility = if (query.isNotEmpty()) View.GONE else View.VISIBLE
            }
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(editable: android.text.Editable?) = Unit
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
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                allIndustries = repository.getAllIndustries() ?: emptyList()
                adapter.updateData(allIndustries)
            } catch (e: UnknownHostException) {
                Log.e(TAG, "No internet connection", e)
                showError("Нет подключения к интернету")
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "Server timeout", e)
                showError("Превышено время ожидания сервера")
            } catch (e: IOException) {
                Log.e(TAG, "Network error", e)
                showError("Ошибка сети")
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun onIndustrySelected(industry: FilterIndustryDto) {
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
                Bundle().apply {
                    putParcelable("selected_industry", industry)
                }
            )
        }
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
