package com.mika.enterprise.albeaandon.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mika.enterprise.albeaandon.MainActivity
import com.mika.enterprise.albeaandon.R
import com.mika.enterprise.albeaandon.core.BaseFragment
import com.mika.enterprise.albeaandon.core.util.AuthInterceptor
import com.mika.enterprise.albeaandon.core.util.Constant.KEY_LANGUAGE
import com.mika.enterprise.albeaandon.core.util.Constant.PROD_URL_ID
import com.mika.enterprise.albeaandon.core.util.Constant.PROD_URL_ZH
import com.mika.enterprise.albeaandon.core.util.EventObserver
import com.mika.enterprise.albeaandon.core.util.getCodeLanguage
import com.mika.enterprise.albeaandon.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private val viewModel: LoginViewModel by viewModels<LoginViewModel>()

    @Inject
    lateinit var authInterceptor: AuthInterceptor

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinner()
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

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            viewModel.spinnerItems
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languangeSpinner.adapter = adapter
        binding.languangeSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedLanguage = viewModel.spinnerItems[position]
                val languageCode = selectedLanguage.getCodeLanguage()
                if (languageCode != viewModel.sharedPreferences.getString(KEY_LANGUAGE, "en")) {
                    viewModel.sharedPreferences.edit().putString(KEY_LANGUAGE, languageCode).apply()
                    authInterceptor.setUpdatedUrl(
                        if (languageCode == "en") PROD_URL_ID
                        else PROD_URL_ZH
                    )
                    activity?.recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }
}
