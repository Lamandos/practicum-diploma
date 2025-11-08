package ru.practicum.android.diploma.presentation.filter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto

class IndustryAdapter(
    private var industries: List<FilterIndustryDto>,
    private val onClick: (FilterIndustryDto) -> Unit
) : RecyclerView.Adapter<IndustryAdapter.IndustryViewHolder>() {

    inner class IndustryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.industry_name)
        fun bind(industry: FilterIndustryDto) {
            nameText.text = industry.name
            itemView.setOnClickListener { onClick(industry) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_industry_for_rv, parent, false)
        return IndustryViewHolder(view)
    }

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        holder.bind(industries[position])
    }

    override fun getItemCount(): Int = industries.size

    fun updateData(newData: List<FilterIndustryDto>) {
        industries = newData
        notifyDataSetChanged()
    }
}
