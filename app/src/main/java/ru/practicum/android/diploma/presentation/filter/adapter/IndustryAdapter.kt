package ru.practicum.android.diploma.presentation.filter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto

class IndustryAdapter(
    private var industries: List<FilterIndustryDto>,
    private val onClick: (FilterIndustryDto) -> Unit
) : RecyclerView.Adapter<IndustryAdapter.IndustryViewHolder>() {

    private var selectedItem: FilterIndustryDto? = null

    inner class IndustryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.industry_name)
        private val checkBox: CheckBox = view.findViewById(R.id.check_industry)

        fun bind(industry: FilterIndustryDto) {
            name.text = industry.name
            checkBox.isChecked = selectedItem?.id == industry.id

            itemView.setOnClickListener {
                selectedItem = industry
                notifyDataSetChanged()
                onClick(industry)
            }
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
