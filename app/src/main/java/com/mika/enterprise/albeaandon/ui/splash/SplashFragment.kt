package com.mika.enterprise.albeaandon.ui.splash

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mika.enterprise.albeaandon.MainActivity
import com.mika.enterprise.albeaandon.core.BaseFragment
import com.mika.enterprise.albeaandon.core.util.Constant.FRAGMENT_KEY_SKIP_SPLASH
import com.mika.enterprise.albeaandon.core.util.Constant.REQUEST_CODE
import com.mika.enterprise.albeaandon.core.util.getVersionName
import com.mika.enterprise.albeaandon.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    private val viewModel: SplashViewModel by viewModels()


    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding {
        val fragmentSkipSplash =
            activity?.intent?.getBooleanExtra(FRAGMENT_KEY_SKIP_SPLASH, false) ?: false
        if (fragmentSkipSplash) {
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
        }
        return FragmentSplashBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE
            )
        }
        binding.tvVersionCode.text = "v: ${context?.getVersionName()}"

        lifecycleScope.launch {
            delay(3000)
            if (viewModel.checkIfUserIsLoggedIn()) {
                (requireActivity() as MainActivity).startMqttService()
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
            } else {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            }

        }
    }
}