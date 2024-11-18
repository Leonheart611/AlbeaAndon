package com.mika.enterprise.albeaandon.ui.finalize

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mika.enterprise.albeaandon.MainActivity
import com.mika.enterprise.albeaandon.MainViewModel
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.core.BaseFragment
import com.mika.enterprise.albeaandon.core.model.response.TicketData
import com.mika.enterprise.albeaandon.core.util.Constant.ESKALASI
import com.mika.enterprise.albeaandon.core.util.Constant.IS_INTERNAL_TEST
import com.mika.enterprise.albeaandon.core.util.EventObserver
import com.mika.enterprise.albeaandon.core.util.convertDateIntoLocalDateTime
import com.mika.enterprise.albeaandon.core.util.mappingColors
import com.mika.enterprise.albeaandon.databinding.FragmentFinalizeBinding
import com.mika.enterprise.albeaandon.ui.util.EscalationDialog
import com.mika.enterprise.albeaandon.ui.util.MessageDialog
import com.mika.enterprise.albeaandon.ui.util.SPVCloseDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FinalizeFragment : BaseFragment<FragmentFinalizeBinding>() {
    private val viewModel: FinalizeViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()
    private val args: FinalizeFragmentArgs by navArgs()


    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFinalizeBinding {
        return FragmentFinalizeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkArgs()
        setupAction()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.helpTicketStatus.observe(viewLifecycleOwner) {
            if (it) showMessageDialog(
                title = getString(R.string.finalize_notify_help_ticket_success),
                message = "",
                buttonText = getString(R.string.finalize_success_button_label)
            ) {}
        }
        viewModel.doneTicketStatus.observe(viewLifecycleOwner) {
            if (it){
                if (viewModel.userDeptSPV()){
                    showMessageDialog(
                        title = getString(R.string.finalize_close_ticket_success),
                        message = "",
                        buttonText = getString(R.string.finalize_success_button_label)
                    ) { findNavController().popBackStack() }
                }
                else
                showMessageDialog(
                    title = getString(R.string.finalize_notify_done_ticket_success),
                    message = "",
                    buttonText = getString(R.string.finalize_success_button_label)
                ) { findNavController().popBackStack() }
            }
        }
        viewModel.escalateTicketStatus.observe(viewLifecycleOwner) {
            if (it) showMessageDialog(
                title = getString(R.string.finalize_escalation_ticket_success),
                message = "",
                buttonText = getString(R.string.finalize_success_button_label)
            ) { findNavController().popBackStack() }
        }
        viewModel.showLoading.observe(viewLifecycleOwner) { if (it) showLoadingDialog() else hideLoadingDialog() }
        viewModel.isUnauthorized.observe(viewLifecycleOwner) {
            if (it) showTokenExpiredDialog {
                (requireActivity() as MainActivity).stopMqttService()
                viewModel.logout()
                findNavController().navigate(FinalizeFragmentDirections.actionFinalizeFragmentToLoginFragment())
            }
        }
        viewModel.errorTicket.observe(viewLifecycleOwner) {
            showMessageDialog(
                title = getString(R.string.general_server_error_title),
                message = getString(R.string.general_server_error_desc, it.code, it.message),
                buttonText = getString(R.string.general_server_error_action_button)
            ) {}
        }
        activityViewModel.nfcValue.observe(viewLifecycleOwner, EventObserver {
            if (it == args.ticketData?.rfid.orEmpty() || IS_INTERNAL_TEST) {
                viewModel.closeTicket(activityViewModel.todoValue.value?.peekContent()?.id ?: 0)
            } else {
                showMessageDialog(
                    title = getString(R.string.nfc_verify_fail_title),
                    message = getString(R.string.nfc_verify_fail_desc, it),
                    buttonText = getString(R.string.nfc_verify_fail_button_label)
                ) {}
            }
        })
    }

    private fun setupAction() = with(binding) {
        tbFinalize.setNavigationOnClickListener { findNavController().popBackStack() }
        btnDone.isGone = args.ticketData?.ticketStatus == ESKALASI
        btnHelp.isGone = args.ticketData?.ticketStatus == ESKALASI
        btnDone.setOnClickListener {
            if (viewModel.userDeptSPV()) {
                setupSpvTodoConfirmDialog()
            } else {
                viewModel.doneTicket()
            }
        }
        btnHelp.setOnClickListener { viewModel.helpTicket() }
        btnEskalation.setOnClickListener {
            val dialog = EscalationDialog(requireContext())
            dialog.setEscalationListener {
                viewModel.escalateTicket(ticketId = viewModel.ticketId, message = it)
            }
            dialog.show()
        }
    }

    private fun checkArgs() {
        if (args.ticketId == 0) args.ticketData?.let { setupHeader(it) }
        else {
            viewModel.getTicketId(args.ticketId)
            viewModel.ticketDetail.observe(viewLifecycleOwner) { setupHeader(it) }
        }
        viewModel.setupUserDept()
    }

    private fun setupHeader(value: TicketData) = with(binding.includeHeader) {
        viewModel.ticketId = value.ticketID
        tvHeaderTicketNo.text =
            getString(R.string.ticket_id_label, value.ticketID.toString())
        tvHeaderCreateDate.text = getString(
            R.string.ticket_date_label,
            value.ticketDate.convertDateIntoLocalDateTime()
        )
        tvHeaderMachloc.text = getString(R.string.ticket_machine_loc_label, value.mchLoc)
        tvHeaderMachno.text = getString(R.string.ticket_machine_label, value.mchNumber)
        tvHeaderStatus.text = getString(R.string.ticket_status_label, value.ticketStatus)
        tvHeaderStatus.setTextColor(
            getColor(
                requireContext(), mappingColors(value.ticketStatus)
            )
        )
        tvHeaderAssignDate.isVisible = value.assignDate != null
        tvHeaderAssignName.isVisible = value.assignTo != null
        tvHeaderAssignName.text = getString(R.string.ticket_assign_label, value.assignTo)
        tvHeaderAssignDate.text = getString(
            R.string.problem_assign_ticket_date,
            value.assignDate?.convertDateIntoLocalDateTime()
        )
        binding.tvFinalizeProblem.text = getString(R.string.ticket_problem_label, value.problem)
        binding.tvFinalizeActionPlan.text =
            getString(R.string.ticket_action_plan_label, value.actionPlan)
        binding.tvEscalationMessage.text = getString(
            R.string.ticket_escalation_message_label,
            value.message
        )
        binding.btnHelp.isVisible = viewModel.userDeptSPV().not()
        binding.btnEskalation.isVisible = viewModel.userDeptSPV().not()
    }

    private fun setupSpvTodoConfirmDialog() {
        val dialog = MessageDialog(requireContext())
        dialog.setTitle(getString(R.string.spv_done_todo_confirmation_title))
        dialog.setMessage(getString(R.string.spv_done_todo_confirmation_message))
        dialog.setConfirmationButton(
            onNoClickListener = {
                dialog.dismiss()
                setupSpvTodoDialog()
            },
            onYesClickListener = {
                dialog.dismiss()
                viewModel.closeTicket(activityViewModel.todoValue.value?.peekContent()?.id ?: 1)
            }
        )
        dialog.show()
    }

    /*    private fun showNfcVerifyDialog() {
            val dialogFragment = NfcVerifyDialog()
            dialogFragment.setCancelable(false)
            dialogFragment.show(childFragmentManager, "nfc_verify_dialog")
        }*/

    private fun setupSpvTodoDialog() {
        val dialog = SPVCloseDialog {
            viewModel.closeTicket(
                activityViewModel.todoValue.value?.peekContent()?.id ?: 0
            )
        }
        dialog.show(childFragmentManager, "spv_close_dialog")
    }
}