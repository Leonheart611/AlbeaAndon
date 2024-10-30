package com.mika.enterprise.albeaandon.ui.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mika.enterprise.albeaandon.MainViewModel
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.databinding.SpvCloseDialogBinding
import com.mika.enterprise.albeaandon.ui.util.ProblemListBottomSheet.Companion.ProblemType
import com.mika.enterprise.albeaandon.ui.util.ProblemListBottomSheet.Companion.ProblemType.GROUP_PROBLEM
import com.mika.enterprise.albeaandon.ui.util.ProblemListBottomSheet.Companion.ProblemType.PROBLEM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SPVCloseDialog(val onclick: () -> Unit) : DialogFragment() {
    private var _binding: SpvCloseDialogBinding? = null
    private val binding get() = _binding!!

    private val activityViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SpvCloseDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etSpvProblemGroup.setOnClickListener {
            resetAllError()
            val btmSheet =
                ProblemListBottomSheet.newInstance(GROUP_PROBLEM, 0)
            btmSheet.show(childFragmentManager, btmSheet.tag)
        }
        binding.etSpvProblem.setOnClickListener {
            val id = activityViewModel.problemGroupValue.value?.peekContent()?.id ?: 0
            if (id == 0) showPreviousDataEmpty()
            else {
                resetAllError()
                val btmSheet = ProblemListBottomSheet.newInstance(PROBLEM, id)
                btmSheet.show(childFragmentManager, btmSheet.tag)
            }

        }
        binding.etSpvTodo.setOnClickListener {
            val id = activityViewModel.problemValue.value?.peekContent()?.id ?: 0
            if (id == 0) showPreviousDataEmpty()
            else {
                resetAllError()
                val btmSheet = ProblemListBottomSheet.newInstance(ProblemType.TODO, id)
                btmSheet.show(childFragmentManager, btmSheet.tag)
            }
        }
        binding.btnSpvSubmit.setOnClickListener {
            dismiss()
            onclick()
        }
        observeActivityViewModel()
    }

    private fun observeActivityViewModel() {
        activityViewModel.problemGroupValue.observe(viewLifecycleOwner) {
            it?.let {
                binding.etSpvProblemGroup.setText(it.peekContent()?.name)
            }
        }
        activityViewModel.problemValue.observe(viewLifecycleOwner) {
            it?.let {
                binding.etSpvProblem.setText(it.peekContent()?.name)
            }
        }
        activityViewModel.todoValue.observe(viewLifecycleOwner) {
            it?.let {
                binding.etSpvTodo.setText(it.peekContent()?.name)
            }
        }
    }

    private fun showPreviousDataEmpty() {
        when {
            binding.etSpvProblemGroup.text.isEmpty() -> {
                binding.etSpvProblemGroup.error = getString(R.string.spv_done_required_message)
            }

            binding.etSpvTodo.text.isEmpty() -> {
                binding.etSpvTodo.error = getString(R.string.spv_done_required_message)
            }
        }
    }

    private fun resetAllError() {
        binding.etSpvProblemGroup.error = null
        binding.etSpvProblem.error = null
        binding.etSpvTodo.error = null
    }


}