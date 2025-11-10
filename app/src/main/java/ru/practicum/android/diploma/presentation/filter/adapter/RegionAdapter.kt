package ru.practicum.android.diploma.presentation.filter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.filtermodels.Region

class RegionAdapter(
    private var regions: List<Region>,
    private val onClick: (Region) -> Unit
) : RecyclerView.Adapter<RegionAdapter.RegionViewHolder>() {

    inner class RegionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.region_name)
        fun bind(region: Region) {
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

    fun updateData(newRegions: List<Region>) {
        regions = newRegions
        notifyDataSetChanged()
    }
}
