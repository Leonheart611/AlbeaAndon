package com.mika.enterprise.albeaandon.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.core.model.response.TicketData
import com.mika.enterprise.albeaandon.core.util.convertDateIntoLocalDateTime
import com.mika.enterprise.albeaandon.core.util.orEmptyDash
import com.mika.enterprise.albeaandon.databinding.HomeViewItemBinding

class HomeItemAdapter(val listener: OnHomeItemClicked? = null) :
    ListAdapter<TicketData, HomeItemAdapter.HomeItemViewHolder>(TicketDiffUtils()) {

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
                    ContextCompat.getColor(
                        itemView.context,
                        mappingColors(item.ticketStatus)
                    )
                )
                root.setOnClickListener { listener?.onClickListener(item) }
            }
        }

        fun mappingColors(status: String): Int {
            return when (status) {
                "NEW" -> R.color.new_color
                "ONPROG" -> R.color.in_progress_color
                "CLOSED" -> R.color.close_color
                else -> R.color.new_color
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