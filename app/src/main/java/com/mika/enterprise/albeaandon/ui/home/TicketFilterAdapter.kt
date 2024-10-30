package com.mika.enterprise.albeaandon.ui.home

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.mika.enterprise.albeaandon.R

class TicketFilterAdapter(
    val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<TicketFilterAdapter.ChipViewHolder>() {

    private val chipList = mutableListOf<FilterData>()
    private var selectedPosition = RecyclerView.NO_POSITION

    fun submit(list: MutableList<FilterData>) {
        chipList.clear()
        chipList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ChipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chipItem: Chip = itemView.findViewById(R.id.chipItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.filter_chip_item, parent, false)
        return ChipViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        val chipData = chipList[position]
        holder.chipItem.text = chipData.text
        holder.chipItem.chipBackgroundColor =
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    holder.itemView.context,
                    if (chipData.isSelected) R.color.selected_chip_background
                    else R.color.chip_background
                )
            )

        holder.chipItem.setOnClickListener {
            chipList.forEachIndexed { index, filterData ->
                filterData.isSelected = index == holder.absoluteAdapterPosition
            }
            selectedPosition = holder.absoluteAdapterPosition
            onItemClick(chipData.text)
            notifyDataSetChanged()

        }
    }

    override fun getItemCount(): Int {
        return chipList.size
    }
}

data class FilterData(
    val text: String,
    var isSelected: Boolean = false
)