package com.mika.enterprise.albeaandon.ui.assign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mika.enterprise.albeaandon.MainActivity
import com.mika.enterprise.albeaandon.MainViewModel
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.core.BaseFragment
import com.mika.enterprise.albeaandon.core.model.response.PersonnelData
import com.mika.enterprise.albeaandon.core.util.Constant.IS_INTERNAL_TEST
import com.mika.enterprise.albeaandon.core.util.Constant.userGroups
import com.mika.enterprise.albeaandon.core.util.EventObserver
import com.mika.enterprise.albeaandon.core.util.convertDateIntoLocalDateTime
import com.mika.enterprise.albeaandon.databinding.FragmentAssignBinding
import com.mika.enterprise.albeaandon.ui.util.NfcVerifyDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssignFragment : BaseFragment<FragmentAssignBinding>(), PersonnelAdapter.OnPersonnelClicked {

    private val viewModel: AssignViewModel by viewModels()
    private val nfcViewModel: MainViewModel by activityViewModels()
    private val adapter = PersonnelAdapter(mutableListOf(), this)
    private val args: AssignFragmentArgs by navArgs()

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAssignBinding {
        return FragmentAssignBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupSpinner()
        setupHeader()
        setupAction()
        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it) showLoadingDialog() else hideLoadingDialog()
        }
        viewModel.personnelList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter.submitList(it, viewModel.isNotSameUserGroup)
            }
        }
        viewModel.isNotAuthorized.observe(viewLifecycleOwner) {
            if (it) showMessageDialog(
                title = getString(R.string.unauthorized_title),
                message = getString(R.string.unauthorized_desc),
                buttonText = getString(R.string.unauthorized_button_label)
            ) { navigateToLogin() }
        }
        viewModel.assignResult.observe(viewLifecycleOwner) {
            if (it) {
                showMessageDialog(
                    title = getString(R.string.assign_success_title),
                    message = getString(
                        R.string.assign_success_desc,
                        viewModel.selectedPersonnel?.userName.orEmpty()
                    ),
                    buttonText = getString(R.string.assign_success_button_label)
                ) { findNavController().popBackStack() }
            }
        }
        nfcViewModel.nfcValue.observe(viewLifecycleOwner, EventObserver {
            if (it == args.ticketData.rfid || IS_INTERNAL_TEST) {
                viewModel.postAssignTicket(
                    username = viewModel.selectedPersonnel?.userName.orEmpty(),
                    ticketId = args.ticketData.ticketID
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

    private fun setupHeader() = with(binding) {
        tvAssignTicketno.text =
            getString(R.string.ticket_id_label, args.ticketData.ticketID.toString())
        tvAssignCreatedate.text = getString(
            R.string.ticket_date_label,
            args.ticketData.ticketDate.convertDateIntoLocalDateTime()
        )
        tvAssignMachloc.text = getString(R.string.ticket_machine_loc_label, args.ticketData.mchLoc)
        tvAssignMachno.text = getString(R.string.ticket_machine_label, args.ticketData.mchNumber)
        tvAssignStatus.text = getString(R.string.ticket_status_label, args.ticketData.ticketStatus)
    }

    private fun setupAction() {
        binding.tbAssign.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnAssign.setOnClickListener {
            if (viewModel.selectedPersonnel != null) {
                val dialogFragment = NfcVerifyDialog()
                dialogFragment.setCancelable(false)
                dialogFragment.show(childFragmentManager, "nfc_verify_dialog")
            } else {
                showMessageDialog(
                    title = getString(R.string.assign_personnel_not_selected_label),
                    message = getString(R.string.assign_personnel_not_selected_desc),
                    buttonText = getString(R.string.assign_personnel_not_selected_button_label)
                ) {}
            }
        }
    }

    private fun setupSpinner() {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userGroups)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnAssignGroup.adapter = adapter
        binding.spnAssignGroup.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.getPersonnelList(userGroups[p2])
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun setupAdapter() = with(binding) {
        rvAssingPersonnellist.adapter = adapter
        rvAssingPersonnellist.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun navigateToLogin() {
        (requireActivity() as MainActivity).stopMqttService()
        viewModel.logout()
        findNavController().navigate(AssignFragmentDirections.actionAssignFragmentToLoginFragment())
    }

    override fun onClickListener(item: PersonnelData) {
        viewModel.selectedPersonnel = item
    }
}