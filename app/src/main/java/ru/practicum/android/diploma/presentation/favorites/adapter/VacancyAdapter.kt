package ru.practicum.android.diploma.presentation.favorites.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.vacancy.Salary
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import ru.practicum.android.diploma.util.formatsalary.formatSalary

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
        private val placeholder: ImageView = itemView.findViewById(R.id.vacancyPlaceholder)

        fun bind(vacancy: VacancyDetails) {
            nameCity.text = "${vacancy.name}\n${vacancy.area}"
            workPlace.text = vacancy.employer
            salary.text = formatSalary(vacancy.salary as Salary?)

            placeholder.setImageResource(R.drawable.placeholder)
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
