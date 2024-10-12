package com.mika.enterprise.albeaandon.ui.assign

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.core.model.response.PersonnelData
import com.mika.enterprise.albeaandon.databinding.AssignPersonnelItemBinding

class PersonnelAdapter(
    private val personnelList: MutableList<PersonnelData>,
    val listener: OnPersonnelClicked? = null
) :
    RecyclerView.Adapter<PersonnelAdapter.PersonnelViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    fun submitList(list: List<PersonnelData>, isNotSameUserGroup: Boolean) {
        if (isNotSameUserGroup) {
            personnelList.clear()
            selectedPosition = RecyclerView.NO_POSITION
            personnelList.addAll(list)
            notifyDataSetChanged()
        } else {
            val oldSize = personnelList.size
            personnelList.addAll(list)
            notifyItemRangeInserted(oldSize, list.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonnelViewHolder {
        return PersonnelViewHolder(
            AssignPersonnelItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PersonnelViewHolder, position: Int) {
        personnelList[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int = personnelList.size

    inner class PersonnelViewHolder(val view: AssignPersonnelItemBinding) : ViewHolder(view.root) {
        fun bind(personnel: PersonnelData) = with(view) {
            tvPersonName.text = view.root.context.getString(
                R.string.assign_personnel_name_label, personnel.userName)
            tvPersonDept.text =
                view.root.context.getString(R.string.assign_personnel_dept_label, personnel.userDept)
            tvPersonTicket.text =
                view.root.context.getString(R.string.assign_personnel_ticket_label, personnel.jumlahTicketOnProgress.toString())
            tvPersonUsergroup.text = personnel.userGroup
            root.isChecked = selectedPosition == bindingAdapterPosition
            root.setOnClickListener {
                selectedPosition = bindingAdapterPosition
                listener?.onClickListener(personnel)
                notifyDataSetChanged()
            }
        }
    }

    interface OnPersonnelClicked {
        fun onClickListener(item: PersonnelData)
    }
}