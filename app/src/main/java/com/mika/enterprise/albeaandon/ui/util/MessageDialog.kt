package com.mika.enterprise.albeaandon.ui.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isGone
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mika.enterprise.albeaandon.databinding.MessageBottomSheetDialogBinding

class MessageDialog(context: Context) : BottomSheetDialog(context) {

    private val binding: MessageBottomSheetDialogBinding by lazy {
        MessageBottomSheetDialogBinding.inflate(LayoutInflater.from(context))
    }

    init {
        setContentView(binding.root).apply {
            setCanceledOnTouchOutside(false)
            behavior.isHideable = false
        }
    }

    fun setTitle(title: String) {
        binding.tvMessageTitle.text = title
    }

    fun setMessage(message: String) {
        binding.tvMessageDesc.text = message
    }

    fun setActionButton(text: String, onClickListener: View.OnClickListener) {
        binding.btnMessageAction.text = text
        binding.btnMessageAction.setOnClickListener(onClickListener)
    }

    fun setConfirmationButton(
        onNoClickListener: View.OnClickListener,
        onYesClickListener: View.OnClickListener
    ) {
        binding.btnMessageAction.isGone = true
        binding.btnNegativeConfirmation.isGone = false
        binding.btnNegativeConfirmation.setOnClickListener(onNoClickListener)
        binding.btnPositiveConfirmation.isGone = false
        binding.btnPositiveConfirmation.setOnClickListener(onYesClickListener)
    }
}