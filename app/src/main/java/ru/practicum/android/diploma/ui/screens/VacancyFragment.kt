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
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.models.vacancydetails.Contacts
import ru.practicum.android.diploma.domain.models.vacancydetails.Salary
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
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
    private var vacancyId: String? = null
    private var fromFavorites: Boolean = false

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

    private fun bindVacancyDetails(details: VacancyDetails) {
        val sectionColor = getSectionColor()
        bindBasics(details)
        bindSkills(details.skills, sectionColor)
        bindContacts(details.contacts)
        bindDescription(details, sectionColor)
        bindEmployerLogo(details.employer?.logo)
    }

    private fun getSectionColor(): Int {
        val titleColor = ContextCompat.getColor(requireContext(), R.color.yp_black)
        val nightMode =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        return if (nightMode) ContextCompat.getColor(requireContext(), R.color.white) else titleColor
    }

    private fun bindBasics(details: VacancyDetails) {
        binding.vacName.text = details.name.orEmpty()
        binding.vacSalary.text = formatSalary(details.salary)
        binding.vacEmployer.text = details.employer?.name.orEmpty().ifBlank { getString(NOT_SPECIFIED_TEXT_RES) }
        binding.vacRegion.text = details.area?.name.orEmpty().ifBlank { getString(NOT_SPECIFIED_TEXT_RES) }
        binding.experienceInfo.text = details.experience?.name.orEmpty().ifBlank { getString(NOT_SPECIFIED_TEXT_RES) }
        binding.scheduleInfo.text = details.schedule?.name.orEmpty().ifBlank { getString(NOT_SPECIFIED_TEXT_RES) }
        setupSectionTitles(getSectionColor())
    }

    private fun setupSectionTitles(sectionColor: Int) {
        fun setupTitle(view: TextView, resId: Int) {
            view.apply {
                text = getString(resId)
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
        return salary?.let {
            val from = it.from?.toString().orEmpty()
            val to = it.to?.toString().orEmpty()
            val currency = it.currency.orEmpty()
            when {
                from.isNotEmpty() && to.isNotEmpty() -> "от $from до $to $currency"
                from.isNotEmpty() -> "от $from $currency"
                to.isNotEmpty() -> "до $to $currency"
                else -> getString(R.string.salary_not_specified)
            }
        } ?: getString(R.string.salary_not_specified)
    }

    private fun bindSkills(skills: List<String>?, sectionColor: Int) {
        if (skills.isNullOrEmpty()) {
            binding.skillsInfo.visibility = View.GONE
            binding.skillsTitle.visibility = View.GONE
            return
        }
        binding.skillsInfo.visibility = View.VISIBLE
        binding.skillsInfo.setTextColor(sectionColor)
        binding.skillsInfo.text = skills.joinToString("\n") { "$BULLET_SYMBOL$it" }
    }

    private fun bindContacts(contacts: Contacts?) {
        val contactText = contacts?.let { c ->
            val lines = mutableListOf<String>()
            c.email?.takeIf { it.isNotBlank() }?.let(lines::add)
            c.phones?.forEach { phone ->
                val number = phone.number.orEmpty()
                if (number.isNotBlank()) {
                    lines.add(
                        if (!phone.comment.isNullOrBlank()) "$number (${phone.comment})" else number
                    )
                }
            }
            lines.joinToString("\n").takeIf { it.isNotBlank() }
        }
        if (contactText.isNullOrBlank()) {
            binding.contactsInfo.visibility = View.GONE
            binding.contactsTitle.visibility = View.GONE
        } else {
            binding.contactsInfo.visibility = View.VISIBLE
            binding.contactsInfo.text = contactText
        }
    }

    private fun bindEmployerLogo(logoUrl: String?) {
        logoUrl?.let {
            Glide.with(binding.vacImg.context)
                .load(it)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.vacImg)
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
        if (text.isBlank()) {
            titleView.visibility = View.GONE
            infoView.visibility = View.GONE
            return
        }
        titleView.visibility = View.VISIBLE
        infoView.visibility = View.VISIBLE
        infoView.text = addBullets(text, sectionColor)
    }

    private fun addBullets(text: String, sectionColor: Int): SpannableStringBuilder {
        val result = SpannableStringBuilder()
        text.lines().filter { it.isNotBlank() }.forEach { line ->
            val bullet = SpannableString(BULLET_SYMBOL).apply {
                setSpan(ForegroundColorSpan(sectionColor), 0, BULLET_SYMBOL.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            result.append(bullet)
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

    private fun setupObservers() {
        viewModel.vacancyDetails.observe(viewLifecycleOwner) { vacancy ->
            handleVacancyDetails(vacancy)
        }
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            handleFavoriteState(isFavorite)
        }
    }

    private fun handleVacancyDetails(vacancy: VacancyDetails?) {
        if (vacancy != null) {
            showVacancy(vacancy)
            bindVacancyDetails(vacancy)
        } else if (fromFavorites) {
            findNavController().popBackStack()
        }
    }

    private fun handleFavoriteState(isFavorite: Boolean) {
        updateFavoritesButton(isFavorite)
        if (fromFavorites && !isFavorite) {
            findNavController().popBackStack()
        }
    }

    private fun showVacancy(vacancy: VacancyDetails) {
        binding.vacName.text = vacancy.name
        binding.vacEmployer.text = vacancy.employer?.name ?: ""
        binding.vacSalary.text = formatSalary(vacancy.salary)
    }

    private fun updateFavoritesButton(isFavorite: Boolean) {
        if (isFavorite) {
            binding.favoritesBtn.setImageResource(R.drawable.favorite_fill_icon)
        } else {
            binding.favoritesBtn.setImageResource(R.drawable.favorite_icon)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
