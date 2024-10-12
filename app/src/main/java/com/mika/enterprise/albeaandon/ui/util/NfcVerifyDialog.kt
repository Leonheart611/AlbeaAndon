package com.mika.enterprise.albeaandon.ui.util

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mika.enterprise.albeaandon.MainViewModel
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.core.util.Event
import com.mika.enterprise.albeaandon.core.util.showToast
import com.mika.enterprise.albeaandon.databinding.NfcVerifyDialogBinding

class NfcVerifyDialog : DialogFragment() {

    private var _binding: NfcVerifyDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private var nfcAdapter: NfcAdapter? = null
    private var nfcManager: NfcManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NfcVerifyDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = NfcVerifyDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
            .setView(binding.root)
        return builder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nfcManager = context?.getSystemService(Context.NFC_SERVICE) as NfcManager
        nfcAdapter = nfcManager?.defaultAdapter
        binding.ivNfcClose.setOnClickListener {
            dismiss()
        }
        if (nfcAdapter?.isEnabled?.not() == true) {
            context?.showToast(getString(R.string.nfc_verify_enable_nfc_message))
            dismiss()
            val intent = Intent(Settings.ACTION_NFC_SETTINGS)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        nfcAdapter?.disableReaderMode(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        nfcAdapter?.disableReaderMode(requireActivity())
    }


    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(
            requireActivity(),
            readerCallback,
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B,
            null
        )
    }

    private val readerCallback = NfcAdapter.ReaderCallback { tag ->
        val id = tag.id.reversedArray()
        val decValue =
            id.joinToString(separator = "") { byte -> "%02X".format(byte) }.toLong(16)
        Log.d("NFC_ID", "Decimal Value: $decValue")
        viewModel.nfcValue.postValue(Event(decValue.toString()))
        dismiss()
    }
}