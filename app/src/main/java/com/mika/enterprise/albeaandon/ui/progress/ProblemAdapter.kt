package com.mika.enterprise.albeaandon.ui.progress

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mika.enterprise.albeaandon.core.model.response.ProblemGeneralResponse
import com.mika.enterprise.albeaandon.databinding.ProblemItemViewBinding

class ProblemAdapter(
    private val onItemClicked: (ProblemGeneralResponse) -> Unit
) : ListAdapter<ProblemGeneralResponse, ProblemAdapter.ProblemViewHolder>(ProblemDiffCallback()),
    Filterable {

    val originalData = mutableListOf<ProblemGeneralResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProblemViewHolder {
        return ProblemViewHolder(
            ProblemItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProblemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProblemViewHolder(private val binding: ProblemItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(problem: ProblemGeneralResponse) {
            binding.tvGroupGeneral.text = problem.name
            binding.root.setOnClickListener {
                onItemClicked(problem)
            }
        }
    }

    fun submitList(list: MutableList<ProblemGeneralResponse>, filter: Boolean = false) {
        if (filter.not()) originalData.addAll(list)
        super.submitList(list)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = if (constraint.isNullOrEmpty()) {
                    originalData
                } else {
                    originalData.filter { item ->
                        item.name.contains(constraint, ignoreCase = true)
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                results.count = filteredList.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                submitList(results?.values as MutableList<ProblemGeneralResponse>, true)
            }

        }
    }

    class ProblemDiffCallback : DiffUtil.ItemCallback<ProblemGeneralResponse>() {
        override fun areItemsTheSame(
            oldItem: ProblemGeneralResponse,
            newItem: ProblemGeneralResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ProblemGeneralResponse,
            newItem: ProblemGeneralResponse
        ): Boolean {
            return oldItem == newItem
        }

    }
}