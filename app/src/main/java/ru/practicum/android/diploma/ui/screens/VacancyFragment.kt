package ru.practicum.android.diploma.ui.screens

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import ru.practicum.android.diploma.presentation.details.VacancyViewModel

class VacancyFragment : Fragment(R.layout.fragment_vacancy) {

    private var _binding: FragmentVacancyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VacancyViewModel by viewModel()

    private val args: VacancyFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVacancyBinding.bind(view)

        setupClickListeners()

        binding.progressBar.visibility = View.VISIBLE
        binding.fullVacInfo.visibility = View.GONE
        binding.vacDelError.visibility = View.GONE
        binding.serverError.visibility = View.GONE

        viewModel.loadVacancyDetails(args.vacancyId)

        viewModel.vacancyDetails.observe(viewLifecycleOwner) { details ->
            binding.progressBar.visibility = View.GONE

            if (details != null) {
                binding.fullVacInfo.visibility = View.VISIBLE
                binding.vacDelError.visibility = View.GONE
                binding.serverError.visibility = View.GONE

                bindVacancyDetails(details)

            } else {
                binding.fullVacInfo.visibility = View.GONE
                binding.vacDelError.visibility = View.GONE
                binding.serverError.visibility = View.VISIBLE
            }
        }
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener { findNavController().navigateUp() }
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

    private fun bindVacancyDetails(details: VacancyDetails) {
        val titleColor = ContextCompat.getColor(requireContext(), R.color.yp_black)
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val sectionColor = if (nightMode) ContextCompat.getColor(requireContext(), R.color.white) else titleColor

        val titleSizeSp = 18f

        fun setupTitle(textView: TextView, stringRes: Int) {
            textView.apply {
                text = getString(stringRes)
                setTextColor(sectionColor)
                textSize = titleSizeSp
                setTypeface(typeface, Typeface.BOLD)
                visibility = View.VISIBLE
            }
        }

        setupTitle(binding.responsibilitiesTitle, R.string.responsibilities)
        setupTitle(binding.requirementsTitle, R.string.requirements)
        setupTitle(binding.termsTitle, R.string.Terms)
        setupTitle(binding.skillsTitle, R.string.skills)
        setupTitle(binding.contactsTitle, R.string.contacts)

        binding.vacName.text = details.name.orEmpty()
        binding.vacSalary.text = details.salary?.let { salary ->
            val from = salary.from?.toString().orEmpty()
            val to = salary.to?.toString().orEmpty()
            val currency = salary.currency.orEmpty()
            when {
                from.isNotEmpty() && to.isNotEmpty() -> "от $from до $to $currency"
                from.isNotEmpty() -> "от $from $currency"
                to.isNotEmpty() -> "до $to $currency"
                else -> getString(R.string.salary_not_specified)
            }
        } ?: getString(R.string.salary_not_specified)

        binding.vacEmployer.text = details.employer?.name.orEmpty().ifBlank { getString(R.string.not_specified) }
        binding.vacRegion.text = details.area?.name.orEmpty().ifBlank { getString(R.string.not_specified) }
        binding.experienceInfo.text = details.experience?.name.orEmpty().ifBlank { getString(R.string.not_specified) }
        binding.scheduleInfo.text = details.schedule?.name.orEmpty().ifBlank { getString(R.string.not_specified) }

        if (!details.skills.isNullOrEmpty()) {
            binding.skillsInfo.visibility = View.VISIBLE
            binding.skillsInfo.text = details.skills.joinToString("\n") { "• $it" }
            binding.skillsInfo.setTextColor(sectionColor)
        } else {
            binding.skillsInfo.visibility = View.GONE
            binding.skillsTitle.visibility = View.GONE
        }

        val contactText = details.contacts?.let { contacts ->
            val contactLines = mutableListOf<String>()
            contacts.email?.takeIf { it.isNotBlank() }?.let { contactLines.add(it) }
            contacts.phones?.forEach { phone ->
                val phoneNumber = phone.number.orEmpty()
                if (phoneNumber.isNotBlank()) {
                    val line = if (!phone.comment.isNullOrBlank()) "$phoneNumber (${phone.comment})" else phoneNumber
                    contactLines.add(line)
                }
            }
            contactLines.joinToString("\n").takeIf { it.isNotBlank() }
        }

        if (!contactText.isNullOrBlank()) {
            binding.contactsInfo.visibility = View.VISIBLE
            binding.contactsInfo.text = contactText
        } else {
            binding.contactsInfo.visibility = View.GONE
            binding.contactsTitle.visibility = View.GONE
        }

        details.employer?.logo?.let { logoUrl ->
            Glide.with(binding.vacImg.context)
                .load(logoUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.vacImg)
        }

        val desc = details.description.orEmpty()

        fun addBullets(text: String?): SpannableStringBuilder {
            val safeText = text.orEmpty()
            if (safeText.isBlank()) return SpannableStringBuilder(getString(R.string.not_specified))
            val result = SpannableStringBuilder()
            safeText.lines().filter { it.isNotBlank() }.forEach { line ->
                val bullet = "• "
                val spannableBullet = SpannableString(bullet)
                spannableBullet.setSpan(
                    ForegroundColorSpan(sectionColor),
                    0, bullet.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                result.append(spannableBullet)
                result.append(line.trim())
                result.append("\n")
            }
            return result
        }

        val responsibilityRegex = Regex("${getString(R.string.responsibilities)}:(.*?)((?=${getString(R.string.requirements)}:)|$)", RegexOption.DOT_MATCHES_ALL)
        val requirementsRegex = Regex("${getString(R.string.requirements)}:(.*?)((?=${getString(R.string.Terms)}:)|$)", RegexOption.DOT_MATCHES_ALL)
        val termsRegex = Regex("${getString(R.string.Terms)}:(.*)", RegexOption.DOT_MATCHES_ALL)

        val responsibilityText = responsibilityRegex.find(desc)?.groups?.get(1)?.value.orEmpty()
        val requirementsText = requirementsRegex.find(desc)?.groups?.get(1)?.value.orEmpty()
        val termsText = termsRegex.find(desc)?.groups?.get(1)?.value.orEmpty()

        binding.responsibilitiesInfo.text = addBullets(responsibilityText)
        binding.requirementsInfo.text = addBullets(requirementsText)

        if (termsText.isNotBlank()) {
            binding.termsTitle.visibility = View.VISIBLE
            binding.termsInfo.visibility = View.VISIBLE
            binding.termsInfo.text = addBullets(termsText)
        } else {
            binding.termsTitle.visibility = View.GONE
            binding.termsInfo.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
