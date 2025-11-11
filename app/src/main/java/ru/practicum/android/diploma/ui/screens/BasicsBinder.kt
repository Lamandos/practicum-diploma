package ru.practicum.android.diploma.ui.screens

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.models.vacancydetails.Salary
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class BasicsBinder {

    companion object {
        private const val TITLE_SIZE_SP = 18f
        private val NOT_SPECIFIED_TEXT_RES = R.string.not_specified
    }

    fun bindBasics(
        binding: FragmentVacancyBinding,
        details: VacancyDetails,
        context: Context
    ) {
        val city = details.address?.city ?: details.area?.name
            ?: context.getString(NOT_SPECIFIED_TEXT_RES)
        binding.vacName.text = "${details.name.orEmpty()} , $city"

        binding.vacSalary.text = formatSalary(details.salary, context)
        val employerName = details.employer?.name.orEmpty()
        binding.vacEmployer.text = employerName.ifBlank {
            context.getString(NOT_SPECIFIED_TEXT_RES)
        }

        val companyAddress = buildCompanyAddress(details, context)
        binding.vacRegion.text = companyAddress

        val experienceText = details.experience?.name.orEmpty()
        binding.experienceInfo.text = experienceText.ifBlank {
            context.getString(NOT_SPECIFIED_TEXT_RES)
        }

        val scheduleText = details.schedule?.name.orEmpty()
        binding.scheduleInfo.text = scheduleText.ifBlank {
            context.getString(NOT_SPECIFIED_TEXT_RES)
        }

        val sectionColor = getSectionColor(context)
        setupSectionTitles(binding, sectionColor, context)
    }

    private fun getSectionColor(context: Context): Int {
        val titleColor = ContextCompat.getColor(context, R.color.yp_black)
        val nightModeFlags = context.resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK
        val isNightMode = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
        return if (isNightMode) ContextCompat.getColor(context, R.color.white) else titleColor
    }

    private fun buildCompanyAddress(details: VacancyDetails, context: Context): String {
        val address = details.address
        return when {
            !address?.street.isNullOrBlank() && !address?.building.isNullOrBlank() ->
                "${address?.street.orEmpty()}, ${address?.building.orEmpty()}"

            !address?.city.isNullOrBlank() -> address?.city.orEmpty()
            !details.area?.name.isNullOrBlank() -> details.area?.name.orEmpty()
            else -> context.getString(NOT_SPECIFIED_TEXT_RES)
        }
    }

    private fun setupSectionTitles(
        binding: FragmentVacancyBinding,
        sectionColor: Int,
        context: Context
    ) {
        fun setupTitle(view: TextView, resId: Int) {
            view.apply {
                text = context.getString(resId)
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

    private fun formatSalary(salary: Salary?, context: Context): String {
        return salary?.let {
            val from = it.from?.toString().orEmpty()
            val to = it.to?.toString().orEmpty()
            val currency = getCurrencySymbol(it.currency)
            when {
                from.isNotEmpty() && to.isNotEmpty() -> "от $from до $to $currency"
                from.isNotEmpty() -> "от $from $currency"
                to.isNotEmpty() -> "до $to $currency"
                else -> context.getString(R.string.salary_not_specified)
            }
        } ?: context.getString(R.string.salary_not_specified)
    }

    private fun getCurrencySymbol(currency: String?): String {
        return when (currency?.trim()?.uppercase()) {
            "RUR", "RUB" -> "₽"
            "BYR", "BYN" -> "Br"
            "USD" -> "$"
            "EUR" -> "€"
            "KZT" -> "₸"
            "UAH" -> "₴"
            "AZN" -> "₼"
            "UZS" -> "so'm"
            "GEL" -> "₾"
            "KGS", "KGT" -> "с"
            else -> currency ?: ""
        }
    }
}
