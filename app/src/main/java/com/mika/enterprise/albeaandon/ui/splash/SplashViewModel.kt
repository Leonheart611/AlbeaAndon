package com.mika.enterprise.albeaandon.ui.splash

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.mika.enterprise.albeaandon.core.util.Constant.USER_TOKEN
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    fun checkIfUserIsLoggedIn(): Boolean {
        return sharedPreferences.getString(USER_TOKEN, null) != null
    }
}