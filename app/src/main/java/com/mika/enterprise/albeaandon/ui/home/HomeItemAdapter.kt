package com.mika.enterprise.albeaandon.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.core.model.response.TicketData
import com.mika.enterprise.albeaandon.core.util.convertDateIntoLocalDateTime
import com.mika.enterprise.albeaandon.core.util.mappingColors
import com.mika.enterprise.albeaandon.core.util.orEmptyDash
import com.mika.enterprise.albeaandon.databinding.HomeViewItemBinding

class HomeItemAdapter(val listener: OnHomeItemClicked? = null) :
    ListAdapter<TicketData, HomeItemAdapter.HomeItemViewHolder>(TicketDiffUtils()), Filterable {
    val originalData = mutableListOf<TicketData>()

    fun submitList(list: List<TicketData>, filter: Boolean = false) {
        if (filter.not()) {
            originalData.clear()
            originalData.addAll(list)
        }
        super.submitList(list)
    }

    fun updateList(list: List<TicketData>) {
        originalData.addAll(list)
        super.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeItemViewHolder {
        return HomeItemViewHolder(
            HomeViewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: HomeItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class HomeItemViewHolder(private val view: HomeViewItemBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(item: TicketData) {
            with(view) {
                tvTicketId.text =
                    view.context.getString(R.string.ticket_id_label, item.ticketID.toString())
                tvTicketStatus.text =
                    view.context.getString(R.string.ticket_status_label, item.ticketStatus)
                tvTicketMachine.text =
                    view.context.getString(R.string.ticket_machine_label, item.mchNumber)
                tvTicketAssign.text =
                    view.context.getString(
                        R.string.ticket_assign_label,
                        item.assignTo.orEmptyDash()
                    )
                tvTicketProblem.text =
                    view.context.getString(
                        R.string.ticket_problem_label,
                        item.problem.orEmptyDash()
                    )
                tvTicketDate.text = view.context.getString(
                    R.string.ticket_date_label,
                    item.ticketDate.convertDateIntoLocalDateTime()
                )
                tvTicketMachineloc.text =
                    view.context.getString(R.string.ticket_machine_loc_label, item.mchLoc)
                tvTicketStatus.setTextColor(
                    getColor(
                        itemView.context, mappingColors(item.ticketStatus)
                    )
                )
                root.setOnClickListener { listener?.onClickListener(item) }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = if (constraint.isNullOrEmpty() || constraint == "ALL") {
                    originalData
                } else {
                    originalData.filter { item ->
                        item.ticketStatus.contains(constraint, ignoreCase = true)
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                results.count = filteredList.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                submitList(results?.values as MutableList<TicketData>, true)
            }

        }
    }

    class TicketDiffUtils : DiffUtil.ItemCallback<TicketData>() {
        override fun areItemsTheSame(oldItem: TicketData, newItem: TicketData): Boolean {
            return oldItem.ticketID == newItem.ticketID
        }

        override fun areContentsTheSame(oldItem: TicketData, newItem: TicketData): Boolean {
            return oldItem == newItem
        }
    }

    interface OnHomeItemClicked {
        fun onClickListener(item: TicketData)
    }
}