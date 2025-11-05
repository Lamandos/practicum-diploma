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
import ru.practicum.android.diploma.domain.models.vacancydetails.Contacts
import ru.practicum.android.diploma.domain.models.vacancydetails.Salary
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import ru.practicum.android.diploma.presentation.details.VacancyResult
import ru.practicum.android.diploma.presentation.details.VacancyViewModel


class VacancyFragment : Fragment(R.layout.fragment_vacancy) {

    companion object {
        private const val BULLET_SYMBOL = "• "
        private const val TITLE_SIZE_SP = 18f
        private val NOT_SPECIFIED_TEXT_RES = R.string.not_specified
    }

    private var _binding: FragmentVacancyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VacancyViewModel by viewModel()
    private val args: VacancyFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVacancyBinding.bind(view)

        setupClickListeners()
        showLoadingState()

        viewModel.loadVacancyDetails(args.vacancyId)
        viewModel.vacancyDetails.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            binding.fullVacInfo.visibility = View.GONE
            binding.vacDelError.visibility = View.GONE
            binding.serverError.visibility = View.GONE

            when (result) {
                is VacancyResult.Success -> {
                    binding.fullVacInfo.visibility = View.VISIBLE
                    bindVacancyDetails(result.data)
                }
                is VacancyResult.Error -> {
                    when (result.code) {
                        404 -> binding.vacDelError.visibility = View.VISIBLE
                        else -> binding.serverError.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener { findNavController().navigateUp() }
        binding.shareBtn.setOnClickListener {
            (viewModel.vacancyDetails.value as? VacancyResult.Success)?.data?.let { vacancy ->
                shareVacancy(vacancy.url)
            }
        }
        ContactsClickHandler.makeLinksClickable(binding.contactsInfo)
    }

    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
        binding.fullVacInfo.visibility = View.GONE
        binding.vacDelError.visibility = View.GONE
        binding.serverError.visibility = View.GONE
    }

    private fun shareVacancy(url: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_vacancy_title)))
    }

    private fun bindVacancyDetails(details: VacancyDetails) {
        val titleColor = ContextCompat.getColor(requireContext(), R.color.yp_black)
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val sectionColor = if (nightMode) ContextCompat.getColor(requireContext(), R.color.white) else titleColor

        setupSectionTitles(sectionColor)

        binding.vacName.text = details.name.orEmpty()
        binding.vacSalary.text = formatSalary(details.salary)
        binding.vacEmployer.text = details.employer?.name.orEmpty().ifBlank { getString(NOT_SPECIFIED_TEXT_RES) }
        binding.vacRegion.text = details.area?.name.orEmpty().ifBlank { getString(NOT_SPECIFIED_TEXT_RES) }
        binding.experienceInfo.text = details.experience?.name.orEmpty().ifBlank { getString(NOT_SPECIFIED_TEXT_RES) }
        binding.scheduleInfo.text = details.schedule?.name.orEmpty().ifBlank { getString(NOT_SPECIFIED_TEXT_RES) }

        bindSkills(details.skills, sectionColor)
        bindContacts(details.contacts)

        details.employer?.logo?.let { logoUrl ->
            Glide.with(binding.vacImg.context)
                .load(logoUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.vacImg)
        }

        bindDescription(details, sectionColor)
    }

    private fun setupSectionTitles(sectionColor: Int) {
        fun setupTitle(textView: TextView, stringRes: Int) {
            textView.apply {
                text = getString(stringRes)
                setTextColor(sectionColor)
                textSize = TITLE_SIZE_SP
                setTypeface(typeface, Typeface.BOLD)
                visibility = View.VISIBLE
            }
        }
        setupTitle(binding.responsibilitiesTitle, R.string.responsibilities)
        setupTitle(binding.requirementsTitle, R.string.requirements)
        setupTitle(binding.termsTitle, R.string.Terms)
        setupTitle(binding.skillsTitle, R.string.skills)
        setupTitle(binding.contactsTitle, R.string.contacts)
    }

    private fun formatSalary(salary: Salary?): String {
        return salary?.let { s ->
            val from = s.from?.toString().orEmpty()
            val to = s.to?.toString().orEmpty()
            val currency = s.currency.orEmpty()
            when {
                from.isNotEmpty() && to.isNotEmpty() -> "от $from до $to $currency"
                from.isNotEmpty() -> "от $from $currency"
                to.isNotEmpty() -> "до $to $currency"
                else -> getString(R.string.salary_not_specified)
            }
        } ?: getString(R.string.salary_not_specified)
    }

    private fun bindSkills(skills: List<String>?, sectionColor: Int) {
        if (!skills.isNullOrEmpty()) {
            binding.skillsInfo.visibility = View.VISIBLE
            binding.skillsInfo.text = skills.joinToString("\n") { "$BULLET_SYMBOL$it" }
            binding.skillsInfo.setTextColor(sectionColor)
        } else {
            binding.skillsInfo.visibility = View.GONE
            binding.skillsTitle.visibility = View.GONE
        }
    }

    private fun bindContacts(contacts: Contacts?) {
        val contactText = contacts?.let { c ->
            val lines = mutableListOf<String>()
            c.email?.takeIf { it.isNotBlank() }?.let { lines.add(it) }
            c.phones?.forEach { phone ->
                val number = phone.number.orEmpty()
                if (number.isNotBlank()) {
                    val line = if (!phone.comment.isNullOrBlank()) "$number (${phone.comment})" else number
                    lines.add(line)
                }
            }
            lines.joinToString("\n").takeIf { it.isNotBlank() }
        }

        if (!contactText.isNullOrBlank()) {
            binding.contactsInfo.visibility = View.VISIBLE
            binding.contactsInfo.text = contactText
        } else {
            binding.contactsInfo.visibility = View.GONE
            binding.contactsTitle.visibility = View.GONE
        }
    }

    private fun bindDescription(details: VacancyDetails, sectionColor: Int) {
        val desc = details.description.orEmpty()
        if (desc.isBlank()) return

        val responsibilityText = extractSection(desc, R.string.responsibilities, R.string.requirements)
        val requirementsText = extractSection(desc, R.string.requirements, R.string.Terms)
        val termsText = extractSection(desc, R.string.Terms, null)

        bindSection(binding.responsibilitiesTitle, binding.responsibilitiesInfo, responsibilityText, sectionColor)
        bindSection(binding.requirementsTitle, binding.requirementsInfo, requirementsText, sectionColor)
        bindSection(binding.termsTitle, binding.termsInfo, termsText, sectionColor)
    }

    private fun bindSection(titleView: TextView, infoView: TextView, text: String, sectionColor: Int) {
        if (text.isNotBlank()) {
            titleView.visibility = View.VISIBLE
            infoView.visibility = View.VISIBLE
            infoView.text = addBullets(text, sectionColor)
        } else {
            titleView.visibility = View.GONE
            infoView.visibility = View.GONE
        }
    }

    private fun addBullets(text: String, sectionColor: Int): SpannableStringBuilder {
        val result = SpannableStringBuilder()
        text.lines()
            .filter { it.isNotBlank() }
            .forEach { line ->
                val spannableBullet = SpannableString(BULLET_SYMBOL).apply {
                    setSpan(
                        ForegroundColorSpan(sectionColor),
                        0,
                        BULLET_SYMBOL.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                result.append(spannableBullet)
                result.append(line.trim())
                result.append("\n")
            }
        return result
    }

    private fun extractSection(desc: String, startRes: Int, endRes: Int?): String {
        val startText = getString(startRes)
        val endText = endRes?.let { getString(it) }

        val startIndex = desc.indexOf("$startText:")
        if (startIndex == -1) return ""

        val from = startIndex + startText.length + 1
        val to = endText?.let {
            val endIndex = desc.indexOf("$it:", from)
            if (endIndex == -1) desc.length else endIndex
        } ?: desc.length

        return desc.substring(from, to).trim()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
