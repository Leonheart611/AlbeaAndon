package com.mika.enterprise.albeaandon.ui.util

import android.content.Context
import android.view.LayoutInflater
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.databinding.EscalationDialogBinding

class EscalationDialog(context: Context) : BottomSheetDialog(context) {

    private val binding: EscalationDialogBinding by lazy {
        EscalationDialogBinding.inflate(LayoutInflater.from(context))
    }

    init {
        setContentView(binding.root).apply {
            setCanceledOnTouchOutside(false)
            behavior.isHideable = false
        }
    }

    fun setEscalationListener(listener: (String) -> Unit) {
        binding.etEscalationMessage.doAfterTextChanged {
            binding.tilEscalationMessage.error = null
        }
        binding.btnEscalate.setOnClickListener {
            val message = binding.etEscalationMessage.text.toString()
            if (message.isBlank()) {
                binding.tilEscalationMessage.error =
                    context.getString(R.string.escalation_error_no_message)
            } else {
                listener(message)
                dismiss()
            }
        }
    }
}