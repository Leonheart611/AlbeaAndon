package com.mika.enterprise.albeaandon.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mika.enterprise.albeaandon.MainActivity
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.core.BaseFragment
import com.mika.enterprise.albeaandon.core.util.EventObserver
import com.mika.enterprise.albeaandon.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val viewModel: LoginViewModel by viewModels<LoginViewModel>()

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handlingResetWhenTyping()
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            if (checkLoginValue(username, password)) viewModel.login(username, password)
        }
        viewModel.loginResponse.observe(viewLifecycleOwner, EventObserver {
            if (it.success) {
                (requireActivity() as MainActivity).startMqttService()
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
            }
        })
        viewModel.showLoading.observe(viewLifecycleOwner) {
            if (it) showLoadingDialog() else hideLoadingDialog()
        }
        viewModel.errorMessage.observe(viewLifecycleOwner, EventObserver {
            showMessageDialog(it) {

            }
        })
    }


    private fun checkLoginValue(username: String, password: String): Boolean {
        var isValid = true
        if (username.isEmpty()) {
            binding.tilEtUsername.error = getString(R.string.login_username_error_label)
            isValid = false
        }
        if (password.isEmpty()) {
            binding.tilEtPassword.error = getString(R.string.login_password_error_label)
            isValid = false
        }
        return isValid
    }

    private fun handlingResetWhenTyping() {
        binding.etUsername.addTextChangedListener {
            binding.tilEtUsername.error = null
        }
        binding.etPassword.addTextChangedListener {
            binding.tilEtPassword.error = null
        }
    }
}
