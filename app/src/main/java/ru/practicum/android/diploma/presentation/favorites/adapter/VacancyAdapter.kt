package ru.practicum.android.diploma.presentation.favorites.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import ru.practicum.android.diploma.util.networkutils.NetworkUtils
import java.text.NumberFormat
import java.util.Locale

class VacancyAdapter(
    private val onItemClick: (VacancyDetails) -> Unit,
    private val context: Context
) : ListAdapter<VacancyDetails, VacancyAdapter.VacancyViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacancyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_vacancy_for_rv, parent, false)
        return VacancyViewHolder(view)
    }

    override fun onBindViewHolder(holder: VacancyViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { onItemClick(getItem(position)) }
    }

    inner class VacancyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameCity: TextView = itemView.findViewById(R.id.vacancyNameCity)
        private val workPlace: TextView = itemView.findViewById(R.id.vacancyWP)
        private val salary: TextView = itemView.findViewById(R.id.vacancySalary)
        private val logoImageView: ImageView = itemView.findViewById(R.id.vacancyPlaceholder)

        fun bind(vacancy: VacancyDetails) {
            nameCity.text = "${vacancy.name}, ${vacancy.area?.name ?: ""}"
            workPlace.text = vacancy.employer?.name ?: ""
            salary.text = formatSalaryForDetails(vacancy.salary)

            val logoUrl = vacancy.employer?.logo

            val shouldLoadLogo = !logoUrl.isNullOrBlank() && NetworkUtils.isInternetAvailable(context)

            if (shouldLoadLogo) {
                Glide.with(itemView)
                    .load(logoUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(logoImageView)
            } else {
                logoImageView.setImageResource(R.drawable.placeholder)
            }
            nameCity.setOnClickListener { onItemClick(vacancy) }
            itemView.setOnClickListener { onItemClick(vacancy) }
        }

        private fun formatSalaryForDetails(
            salary: ru.practicum.android.diploma.domain.models.vacancydetails.Salary?
        ): String {
            salary ?: return "Зарплата не указана"

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
                else -> "Зарплата не указана"
            }
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
    }

    companion object DiffCallback : DiffUtil.ItemCallback<VacancyDetails>() {
        override fun areItemsTheSame(oldItem: VacancyDetails, newItem: VacancyDetails): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VacancyDetails, newItem: VacancyDetails): Boolean {
            return oldItem == newItem
        }
    }
}
