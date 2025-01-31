package com.mika.enterprise.albeaandon.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.core.util.ErrorResponse
import com.mika.enterprise.albeaandon.ui.util.LoadingDialog
import com.mika.enterprise.albeaandon.ui.util.MessageDialog

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateViewBinding(inflater, container)
        return binding.root
    }

    abstract fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showLoadingDialog() {
        val loadingDialog = LoadingDialog()
        loadingDialog.show(this.parentFragmentManager, "loading_dialog")
    }

    fun hideLoadingDialog() {
        val loadingDialog =
            this.parentFragmentManager.findFragmentByTag("loading_dialog") as? LoadingDialog
        loadingDialog?.dismiss()
    }

    fun showMessageDialog(errorResponse: ErrorResponse, onRetry: () -> Unit) {
        val dialog = MessageDialog(requireContext())
        dialog.setTitle("Failed Retrieve Connection Error ${errorResponse.code}")
        dialog.setMessage("${errorResponse.message}")
        dialog.setActionButton("Ok") {
            onRetry()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showMessageDialog(title: String, message: String, buttonText: String, onRetry: () -> Unit) {
        val dialog = MessageDialog(requireContext())
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setActionButton(buttonText) {
            onRetry()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showTokenExpiredDialog(nextAction: () -> Unit) {
        val dialog = MessageDialog(requireContext())
        dialog.setTitle(getString(R.string.unauthorized_title))
        dialog.setMessage(getString(R.string.unauthorized_desc))
        dialog.setActionButton(getString(R.string.unauthorized_button_label)) {
            dialog.dismiss()
            nextAction()
        }
        dialog.show()
    }
}