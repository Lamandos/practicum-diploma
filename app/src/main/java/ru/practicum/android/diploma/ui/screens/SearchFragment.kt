package ru.practicum.android.diploma.ui.screens

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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

    private var searchText = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchBinding.bind(view)

        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // пока пусто
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.clearIcon.visibility = clearIconVisibility(s)
                binding.searchIcon.visibility = searchIconVisibility(s)
                searchText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // пока пусто
            }

        })

        binding.clearIcon.setOnClickListener {
            binding.searchField.setText("")
            closeKeyboard(binding.searchField)
        }

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                true
            }
            false
        }

        // setupNavigationWithDebounce()
    }

    private fun clearIconVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun searchIconVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun closeKeyboard(view: View) {
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
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
