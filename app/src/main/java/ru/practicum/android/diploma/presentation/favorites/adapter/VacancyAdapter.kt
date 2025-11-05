package ru.practicum.android.diploma.presentation.favorites.adapter

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

class VacancyAdapter(
    private val onItemClick: (VacancyDetails) -> Unit
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
            nameCity.text = "${vacancy.name}\n${vacancy.area?.name ?: ""}"
            workPlace.text = vacancy.employer?.name ?: ""
            salary.text = formatSalaryForDetails(vacancy.salary)

            // Загрузка логотипа аналогично SearchVacancyAdapter
            val logoUrl = vacancy.employer?.logo
            if (!logoUrl.isNullOrBlank()) {
                Glide.with(itemView)
                    .load(logoUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(logoImageView)
            } else {
                // Если логотипа нет, устанавливаем плейсхолдер
                logoImageView.setImageResource(R.drawable.placeholder)
            }
        }

        private fun formatSalaryForDetails(
            salary: ru.practicum.android.diploma.domain.models.vacancydetails.Salary?
        ): String {
            salary ?: return "Зарплата не указана"
            val from = salary.from
            val to = salary.to
            val currency = salary.currency ?: ""
            return when {
                from != null && to != null -> "от $from до $to $currency"
                from != null -> "от $from $currency"
                to != null -> "до $to $currency"
                else -> "Зарплата не указана"
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
