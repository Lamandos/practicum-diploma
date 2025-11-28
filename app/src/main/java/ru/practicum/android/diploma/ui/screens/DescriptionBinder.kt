package ru.practicum.android.diploma.ui.screens

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class DescriptionBinder {

    companion object {
        private const val BULLET_SYMBOL = "• "
    }

    fun bindDescription(
        binding: FragmentVacancyBinding,
        details: VacancyDetails,
        sectionColor: Int,
        context: Context
    ) {
        val desc = details.description.orEmpty()
        if (desc.isBlank()) return

        val responsibilityText = extractSection(
            desc,
            R.string.responsibilities,
            R.string.requirements,
            context
        )
        val requirementsText = extractSection(
            desc,
            R.string.requirements,
            R.string.Terms,
            context
        )
        val termsText = extractSection(desc, R.string.Terms, null, context)

        bindSection(
            binding.responsibilitiesTitle,
            binding.responsibilitiesInfo,
            responsibilityText,
            sectionColor,
            context
        )
        bindSection(
            binding.requirementsTitle,
            binding.requirementsInfo,
            requirementsText,
            sectionColor,
            context
        )
        bindSection(
            binding.termsTitle,
            binding.termsInfo,
            termsText,
            sectionColor,
            context
        )
    }

    private fun bindSection(
        titleView: TextView,
        infoView: TextView,
        text: String,
        sectionColor: Int,
        context: Context
    ) {
        if (text.isBlank()) {
            titleView.visibility = TextView.GONE
            infoView.visibility = TextView.GONE
            return
        }
        titleView.visibility = TextView.VISIBLE
        infoView.visibility = TextView.VISIBLE
        infoView.text = addBullets(text, sectionColor)
    }

    private fun addBullets(text: String, sectionColor: Int): SpannableStringBuilder {
        val result = SpannableStringBuilder()
        text.lines().filter { it.isNotBlank() }.forEach { line ->
            val bullet = SpannableString(BULLET_SYMBOL).apply {
                setSpan(
                    ForegroundColorSpan(sectionColor),
                    0,
                    BULLET_SYMBOL.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            result.append(bullet)
            result.append(line.trim())
            result.append("\n")
        }
        return result
    }

    private fun extractSection(
        desc: String,
        startRes: Int,
        endRes: Int?,
        context: Context
    ): String {
        val startText = context.getString(startRes)
        val endText = endRes?.let { context.getString(it) }
        val startIndex = desc.indexOf("$startText:")
        if (startIndex == -1) return ""
        val from = startIndex + startText.length + 1
        val to = endText?.let {
            val endIndex = desc.indexOf("$it:", from)
            if (endIndex == -1) desc.length else endIndex
        } ?: desc.length
        return desc.substring(from, to).trim()
    }
}
