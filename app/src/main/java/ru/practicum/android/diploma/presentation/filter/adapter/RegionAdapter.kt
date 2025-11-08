package ru.practicum.android.diploma.presentation.filter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.dto.filterdto.FilterAreaDto

class RegionAdapter(
    private var regions: List<FilterAreaDto>,
    private val onClick: (FilterAreaDto) -> Unit
) : RecyclerView.Adapter<RegionAdapter.RegionViewHolder>() {

    inner class RegionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.region_name)
        fun bind(region: FilterAreaDto) {
            nameText.text = region.name
            itemView.setOnClickListener { onClick(region) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_region_for_rv, parent, false)
        return RegionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        holder.bind(regions[position])
    }

    override fun getItemCount(): Int = regions.size

    fun updateData(newRegions: List<FilterAreaDto>) {
        regions = newRegions
        notifyDataSetChanged()
    }
}
