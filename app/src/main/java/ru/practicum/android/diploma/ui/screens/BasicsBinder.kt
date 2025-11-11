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
import java.text.NumberFormat
import java.util.Locale

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

    private fun getCurrencySymbol(currencyCode: String?): String {
        if (currencyCode.isNullOrBlank()) return ""
        return when (currencyCode.uppercase()) {
            "RUB", "RUR" -> "₽"
            "BYR" -> "Br"
            "USD" -> "$"
            "EUR" -> "€"
            "KZT" -> "₸"
            "UAH" -> "₴"
            "AZN" -> "₼"
            "UZS" -> "so'm"
            "GEL" -> "₾"
            "KGT" -> "сом"
            else -> currencyCode.uppercase()
        }
    }

    private fun formatSalary(salary: Salary?, context: Context): String {
        salary ?: return context.getString(R.string.salary_not_specified)

        val numberFormatter = NumberFormat.getInstance(Locale("ru"))

        val fromFormatted = salary.from?.let { numberFormatter.format(it) }
        val toFormatted = salary.to?.let { numberFormatter.format(it) }
        val currencySymbol = getCurrencySymbol(salary.currency)

        return when {
            !fromFormatted.isNullOrBlank() && !toFormatted.isNullOrBlank() -> {
                val range = "от $fromFormatted до $toFormatted"
                "$range $currencySymbol"
            }
            !fromFormatted.isNullOrBlank() -> "от $fromFormatted $currencySymbol"
            !toFormatted.isNullOrBlank() -> "до $toFormatted $currencySymbol"
            else -> context.getString(R.string.salary_not_specified)
        }
    }

}
