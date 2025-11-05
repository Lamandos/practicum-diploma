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
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Vacancy>()
    private var showLoading = false

    companion object {
        private const val TYPE_VACANCY = 0
        private const val TYPE_LOADING = 1
    }

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
                .load(vacancy.employer.logo)
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

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getItemViewType(position: Int): Int {
        return if (showLoading && position == items.size) {
            TYPE_LOADING
        } else {
            TYPE_VACANCY
        }
    }

    override fun getItemCount(): Int = items.size + if (showLoading) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LOADING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_progress, parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_vacancy_for_rv, parent, false)
                VacancyViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VacancyViewHolder -> {
                if (position < items.size) {
                    holder.bind(items[position])
                }
            }
            is LoadingViewHolder -> {
            }
        }
    }

    fun setItems(newItems: List<Vacancy>) {
        val wasShowingLoading = showLoading

        if (wasShowingLoading) {
            showLoading = false
            notifyItemRemoved(items.size)
        }

        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()

        if (wasShowingLoading) {
            showLoading = true
            notifyItemInserted(items.size)
        }
    }

    fun showLoading(loading: Boolean) {
        val wasShowing = showLoading
        showLoading = loading

        when {
            loading && !wasShowing -> {
                notifyItemInserted(items.size)
            }
            !loading && wasShowing -> {
                notifyItemRemoved(items.size)
            }
        }
    }
}
