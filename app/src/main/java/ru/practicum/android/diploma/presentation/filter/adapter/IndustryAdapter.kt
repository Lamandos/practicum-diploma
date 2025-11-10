package ru.practicum.android.diploma.presentation.filter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto

class IndustryAdapter(
    private var industries: List<FilterIndustryDto>,
    private val onIndustryClick: (FilterIndustryDto) -> Unit
) : RecyclerView.Adapter<IndustryAdapter.IndustryViewHolder>() {

    private var selectedIndustry: FilterIndustryDto? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_industry_for_rv, parent, false)
        return IndustryViewHolder(view)
    }

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        holder.bind(industries[position])
    }

    override fun getItemCount(): Int = industries.size

    fun updateData(newIndustries: List<FilterIndustryDto>) {
        industries = newIndustries
        notifyDataSetChanged()
    }

    inner class IndustryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val industryName: TextView = itemView.findViewById(R.id.industry_name)
        private val checkBox: MaterialCheckBox = itemView.findViewById(R.id.check_industry)

        fun bind(industry: FilterIndustryDto) {
            industryName.text = industry.name
            checkBox.isChecked = selectedIndustry?.id == industry.id

            // Обработчик клика на весь item
            itemView.setOnClickListener {
                selectIndustry(industry)
            }

            // Обработчик клика на checkbox
            checkBox.setOnClickListener {
                selectIndustry(industry)
            }
        }

        private fun selectIndustry(industry: FilterIndustryDto) {
            val previousSelected = selectedIndustry
            selectedIndustry = industry

            // Уведомляем об изменении предыдущего выбранного элемента
            previousSelected?.let { oldIndustry ->
                val oldPosition = industries.indexOfFirst { it.id == oldIndustry.id }
                if (oldPosition != -1) {
                    notifyItemChanged(oldPosition)
                }
            }

            // Уведомляем об изменении нового выбранного элемента
            val newPosition = industries.indexOfFirst { it.id == industry.id }
            if (newPosition != -1) {
                notifyItemChanged(newPosition)
            }

            onIndustryClick(industry)
        }
    }

    // Метод для сброса выбора
    fun clearSelection() {
        selectedIndustry = null
        notifyDataSetChanged()
    }

    // Метод для установки выбранной отрасли
    fun setSelectedIndustry(industry: FilterIndustryDto?) {
        selectedIndustry = industry
        notifyDataSetChanged()
    }
}
