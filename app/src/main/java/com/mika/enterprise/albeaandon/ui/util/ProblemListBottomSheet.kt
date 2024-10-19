package com.mika.enterprise.albeaandon.ui.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mika.enterprise.albeaandon.MainViewModel
import com.mika.enterprise.albeaandon.core.model.response.ProblemGeneralResponse
import com.mika.enterprise.albeaandon.core.util.Event
import com.mika.enterprise.albeaandon.databinding.ProblemListBottomSheetBinding
import com.mika.enterprise.albeaandon.ui.progress.ProblemAdapter
import com.mika.enterprise.albeaandon.ui.progress.ProgressViewModel
import com.mika.enterprise.albeaandon.ui.util.ProblemListBottomSheet.Companion.ProblemType.GROUP_PROBLEM
import com.mika.enterprise.albeaandon.ui.util.ProblemListBottomSheet.Companion.ProblemType.PROBLEM
import com.mika.enterprise.albeaandon.ui.util.ProblemListBottomSheet.Companion.ProblemType.TODO
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProblemListBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: ProblemListBottomSheetBinding
    private val viewModel: ProgressViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProblemListBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val enumType = arguments?.getString(PROBLEM_TYPE)?.let { ProblemType.valueOf(it) }
        val problemId = arguments?.getInt(PROBLEM_ID) ?: 0
        val adapter = ProblemAdapter {
            when (enumType) {
                GROUP_PROBLEM -> {
                    activityViewModel.problemGroupValue.postValue(Event(it))
                    activityViewModel.problemValue.postValue(Event(ProblemGeneralResponse(0, "")))
                    activityViewModel.todoValue.postValue(Event(ProblemGeneralResponse(0, "")))
                }

                PROBLEM -> {
                    activityViewModel.problemValue.postValue(Event(it))
                    activityViewModel.todoValue.postValue(Event(ProblemGeneralResponse(0, "")))
                }

                TODO -> activityViewModel.todoValue.postValue(Event(it))
                null -> TODO()
            }
            dismiss()
        }
        setAdapter(adapter)
        when (enumType) {
            GROUP_PROBLEM -> {
                viewModel.getProblemGroup()
                viewModel.problemGroupResponse.observe(viewLifecycleOwner) {
                    if (it.success) {
                        adapter.submitList(it.problemGroup.toMutableList())
                    }
                }
            }

            PROBLEM -> {
                viewModel.getProblem(problemId)
                viewModel.problemGroupResponse.observe(viewLifecycleOwner) {
                    if (it.success) {
                        adapter.submitList(it.problemGroup.toMutableList())
                    }
                }
            }

            TODO -> {
                viewModel.getTodoProblem(problemId)
                viewModel.problemGroupResponse.observe(viewLifecycleOwner) {
                    if (it.success) {
                        adapter.submitList(it.problemGroup.toMutableList())
                    }
                }
            }

            else -> {}
        }
    }

    private fun setAdapter(adapter: ProblemAdapter) {
        binding.rvProblemList.adapter = adapter
        binding.rvProblemList.layoutManager = LinearLayoutManager(requireContext())
        binding.tvProblemSearch.doAfterTextChanged { adapter.filter.filter(it.toString()) }
    }

    companion object {
        enum class ProblemType {
            GROUP_PROBLEM, PROBLEM, TODO
        }

        private const val PROBLEM_TYPE = "PROBLEM_TYPE"
        private const val PROBLEM_ID = "PROBLEM_ID"
        fun newInstance(problemType: ProblemType, problemId: Int): ProblemListBottomSheet {
            val fragment = ProblemListBottomSheet()
            val args = Bundle()
            args.putString(PROBLEM_TYPE, problemType.name)
            args.putInt(PROBLEM_ID, problemId)
            fragment.arguments = args
            return fragment
        }
    }

}