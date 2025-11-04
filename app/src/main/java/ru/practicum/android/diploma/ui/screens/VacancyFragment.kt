package ru.practicum.android.diploma.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.presentation.details.VacancyViewModel

class VacancyFragment : Fragment(R.layout.fragment_vacancy) {

    private var _binding: FragmentVacancyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VacancyViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVacancyBinding.bind(view)

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.shareBtn.setOnClickListener {
            viewModel.vacancyDetails.value?.let { vacancy ->
                shareVacancy(vacancy.url)
            }
        }

        ContactsClickHandler.makeLinksClickable(binding.contactsInfo)
    }

    private fun shareVacancy(url: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, "Поделиться вакансией"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
