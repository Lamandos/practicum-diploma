package ru.practicum.android.diploma.presentation.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.vacancy.Salary
import ru.practicum.android.diploma.domain.models.vacancy.Vacancy

class SearchVacancyAdapter(
    private val onItemClick: (Vacancy) -> Unit
) : RecyclerView.Adapter<SearchVacancyAdapter.VacancyViewHolder>() {

    private val items = mutableListOf<Vacancy>()

    inner class VacancyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameCity: TextView = itemView.findViewById(R.id.vacancyNameCity)
        private val workPlace: TextView = itemView.findViewById(R.id.vacancyWP)
        private val salary: TextView = itemView.findViewById(R.id.vacancySalary)
        private val placeholder: ImageView = itemView.findViewById(R.id.vacancyPlaceholder)

        fun bind(vacancy: Vacancy) {
            nameCity.text = "${vacancy.name}, ${vacancy.area.name}"
            workPlace.text = vacancy.employer.name
            salary.text = formatSalary(vacancy.salary)

            Glide.with(itemView)
                .load(vacancy.employer.logoUrls)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(placeholder)

            itemView.setOnClickListener { onItemClick(vacancy) }
        }

        private fun formatSalary(salary: Salary?): String {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacancyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_vacancy_for_rv, parent, false)
        return VacancyViewHolder(view)
    }

    override fun onBindViewHolder(holder: VacancyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<Vacancy>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
