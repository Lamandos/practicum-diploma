package ru.practicum.android.diploma.ui.screens

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.models.vacancydetails.Contacts
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import ru.practicum.android.diploma.util.networkutils.NetworkUtils

class VacancyBinder {

    private val basicsBinder = BasicsBinder()
    private val descriptionBinder = DescriptionBinder()

    fun bindVacancyDetails(
        binding: FragmentVacancyBinding,
        details: VacancyDetails,
        context: Context
    ) {
        val sectionColor = getSectionColor(context)
        basicsBinder.bindBasics(binding, details, context)
        bindSkills(binding, details.skills, sectionColor, context)
        bindContacts(binding, details.contacts, context)
        descriptionBinder.bindDescription(binding, details, sectionColor, context)
        bindEmployerLogo(binding, details.employer?.logo, context)
    }

    private fun getSectionColor(context: Context): Int {
        val titleColor = ContextCompat.getColor(context, R.color.yp_black)
        val nightModeFlags = context.resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK
        val isNightMode = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
        return if (isNightMode) ContextCompat.getColor(context, R.color.white) else titleColor
    }

    private fun bindSkills(
        binding: FragmentVacancyBinding,
        skills: List<String>?,
        sectionColor: Int,
        context: Context
    ) {
        if (skills.isNullOrEmpty()) {
            binding.skillsInfo.visibility = View.GONE
            binding.skillsTitle.visibility = View.GONE
            return
        }
        binding.skillsInfo.visibility = View.VISIBLE
        binding.skillsInfo.setTextColor(sectionColor)
        binding.skillsInfo.text = skills.joinToString("\n") { "• $it" }
    }

    private fun bindContacts(
        binding: FragmentVacancyBinding,
        contacts: Contacts?,
        context: Context
    ) {
        val contactText = buildContactText(contacts)
        if (contactText.isNullOrBlank()) {
            binding.contactsInfo.visibility = View.GONE
            binding.contactsTitle.visibility = View.GONE
        } else {
            binding.contactsInfo.visibility = View.VISIBLE
            binding.contactsInfo.text = contactText
        }
    }

    private fun buildContactText(contacts: Contacts?): String? {
        if (contacts == null) return null

        val lines = mutableListOf<String>()

        // Добавляем email если есть
        contacts.email?.takeIf { it.isNotBlank() }?.let { email ->
            lines.add(email)
        }

        // Добавляем телефоны если есть
        contacts.phones?.forEach { phone ->
            val number = phone.number.orEmpty()
            if (number.isNotBlank()) {
                val phoneLine = buildPhoneLine(number, phone.comment)
                lines.add(phoneLine)
            }
        }

        return lines.joinToString("\n").takeIf { it.isNotBlank() }
    }

    private fun buildPhoneLine(number: String, comment: String?): String {
        return if (!comment.isNullOrBlank()) {
            "$number ($comment)"
        } else {
            number
        }
    }

    private fun bindEmployerLogo(
        binding: FragmentVacancyBinding,
        logoUrl: String?,
        context: Context
    ) {
        val shouldLoadLogo = !logoUrl.isNullOrBlank() &&
            NetworkUtils.isInternetAvailable(context)

        if (shouldLoadLogo) {
            Glide.with(binding.vacImg.context)
                .load(logoUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.vacImg)
        } else {
            binding.vacImg.setImageResource(R.drawable.placeholder)
        }
    }
}
