package com.mika.enterprise.albeaandon.ui.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mika.enterprise.albeaandon.MainViewModel
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.core.BaseFragment
import com.mika.enterprise.albeaandon.core.util.Constant.IS_INTERNAL_TEST
import com.mika.enterprise.albeaandon.core.util.Constant.ONPROG
import com.mika.enterprise.albeaandon.core.util.EventObserver
import com.mika.enterprise.albeaandon.core.util.convertDateIntoLocalDateTime
import com.mika.enterprise.albeaandon.core.util.mappingColors
import com.mika.enterprise.albeaandon.databinding.FragmentOnprogressBinding
import com.mika.enterprise.albeaandon.ui.util.NfcVerifyDialog
import com.mika.enterprise.albeaandon.ui.util.ProblemListBottomSheet
import com.mika.enterprise.albeaandon.ui.util.ProblemListBottomSheet.Companion.ProblemType.GROUP_PROBLEM
import com.mika.enterprise.albeaandon.ui.util.ProblemListBottomSheet.Companion.ProblemType.PROBLEM
import com.mika.enterprise.albeaandon.ui.util.ProblemListBottomSheet.Companion.ProblemType.TODO
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProgressFragment : BaseFragment<FragmentOnprogressBinding>() {

    private val viewModel: ProgressViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()
    private val args: ProgressFragmentArgs by navArgs()

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnprogressBinding {
        return FragmentOnprogressBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        observeViewModel()
        setupView()
    }

    private fun setupHeader() = with(binding.includeHeader) {
        tvHeaderTicketNo.text =
            getString(R.string.ticket_id_label, args.ticketData.ticketID.toString())
        tvHeaderCreateDate.text = getString(
            R.string.ticket_date_label,
            args.ticketData.ticketDate.convertDateIntoLocalDateTime()
        )
        tvHeaderMachloc.text = getString(R.string.ticket_machine_loc_label, args.ticketData.mchLoc)
        tvHeaderMachno.text = getString(R.string.ticket_machine_label, args.ticketData.mchNumber)
        tvHeaderStatus.text = getString(R.string.ticket_status_label, args.ticketData.ticketStatus)
        tvHeaderStatus.setTextColor(
            getColor(
                requireContext(), mappingColors(args.ticketData.ticketStatus)
            )
        )
        tvHeaderAssignDate.isVisible = args.ticketData.assignDate != null
        tvHeaderAssignName.isVisible = args.ticketData.assignTo != null
        tvHeaderAssignName.text = getString(R.string.ticket_assign_label, args.ticketData.assignTo)
        tvHeaderAssignDate.text = getString(
            R.string.problem_assign_ticket_date,
            args.ticketData.assignDate?.convertDateIntoLocalDateTime()
        )
    }

    private fun setupView() = with(binding) {
        groupOnprogButton.isVisible = args.ticketData.ticketStatus == ONPROG
        btnSubmit.isVisible = args.ticketData.ticketStatus != ONPROG
        etProblemGroup.setOnClickListener {
            val btmSheet =
                ProblemListBottomSheet.newInstance(GROUP_PROBLEM, 0)
            btmSheet.show(childFragmentManager, btmSheet.tag)
        }
        etProblem.setOnClickListener {
            val id = activityViewModel.problemGroupValue.value?.peekContent()?.id ?: 0
            if (id == 0) showPreviousDataEmpty()
            else {
                val btmSheet = ProblemListBottomSheet.newInstance(PROBLEM, id)
                btmSheet.show(childFragmentManager, btmSheet.tag)
            }

        }
        etTodoPlan.setOnClickListener {
            val id = activityViewModel.problemValue.value?.peekContent()?.id ?: 0
            if (id == 0) showPreviousDataEmpty()
            else {
                val btmSheet = ProblemListBottomSheet.newInstance(TODO, id)
                btmSheet.show(childFragmentManager, btmSheet.tag)
            }
        }
        tbProgress.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        btnSubmit.setOnClickListener {
            val todoId = activityViewModel.todoValue.value?.peekContent()?.id ?: 0
            if (todoId == 0) showMessageDialog(
                title = getString(R.string.problem_error_no_todo_id_selected),
                message = "",
                buttonText = getString(R.string.problem_error_no_todo_id_selected_action)
            ) {}
            else {
                val dialogFragment = NfcVerifyDialog()
                dialogFragment.setCancelable(false)
                dialogFragment.show(childFragmentManager, "nfc_verify_dialog")
            }
        }
    }


    private fun observeViewModel() {
        viewModel.onProgressResponse.observe(viewLifecycleOwner) {
            showMessageDialog(
                title = getString(R.string.onprogress_success_title),
                message = "",
                buttonText = getString(R.string.onprogress_success_button_label)
            ) {
                val action =
                    ProgressFragmentDirections.actionProgressFragmentToFinalizeFragment(args.ticketData.ticketID)
                findNavController().navigate(action)
            }
        }
        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it) showLoadingDialog() else hideLoadingDialog()
        }
        viewModel.isUnAuthorized.observe(viewLifecycleOwner) {
            if (it) {
                showTokenExpiredDialog { findNavController().navigate(ProgressFragmentDirections.actionProgressFragmentToLoginFragment()) }
            }
        }
        activityViewModel.problemGroupValue.observe(viewLifecycleOwner, EventObserver {
            binding.etProblemGroup.setText(it.name)
        })
        activityViewModel.problemValue.observe(viewLifecycleOwner, EventObserver {
            binding.etProblem.setText(it.name)
        })
        activityViewModel.todoValue.observe(viewLifecycleOwner, EventObserver {
            binding.etTodoPlan.setText(it.name)
        })
        activityViewModel.nfcValue.observe(viewLifecycleOwner, EventObserver {
            if (it == args.ticketData.rfid || IS_INTERNAL_TEST) {
                viewModel.postOnProgressTicket(
                    args.ticketData.ticketID,
                    activityViewModel.todoValue.value?.peekContent()?.id ?: 0
                )
            } else {
                showMessageDialog(
                    title = getString(R.string.nfc_verify_fail_title),
                    message = getString(R.string.nfc_verify_fail_desc, it),
                    buttonText = getString(R.string.nfc_verify_fail_button_label)
                ) {}
            }
        })
    }

    private fun showPreviousDataEmpty() {
        showMessageDialog(
            title = getString(R.string.problem_error_no_previous_value),
            message = "",
            buttonText = getString(R.string.problem_error_no_previous_value_action)
        ) {}
    }
}