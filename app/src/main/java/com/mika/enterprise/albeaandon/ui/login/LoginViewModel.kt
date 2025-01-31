package com.mika.enterprise.albeaandon.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mika.enterprise.albeaandon.core.model.response.LoginResponse
import com.mika.enterprise.albeaandon.core.repository.NetworkRepository
import com.mika.enterprise.albeaandon.core.util.ErrorResponse
import com.mika.enterprise.albeaandon.core.util.Event
import com.mika.enterprise.albeaandon.core.util.ResultResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _loginResponse = MutableLiveData<Event<LoginResponse>>()
    val loginResponse: LiveData<Event<LoginResponse>> = _loginResponse
    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    val errorMessage = MutableLiveData<Event<ErrorResponse>>()

    fun login(username: String, password: String) {
        _showLoading.postValue(true)
        viewModelScope.launch {
            networkRepository.login(username, password).collect {
                when (it) {
                    is ResultResponse.Success -> {
                        _showLoading.postValue(false)
                        _loginResponse.postValue(Event(it.data))
                    }

                    is ResultResponse.Error -> {
                        _showLoading.postValue(false)
                        errorMessage.postValue(Event(it.errorResponse))
                    }

                    is ResultResponse.UnAuthorized -> {
                        _showLoading.postValue(false)
                        errorMessage.postValue(Event(it.errorResponse))
                    }

                    is ResultResponse.EmptyOrNotFound -> {
                        _showLoading.postValue(false)
                        errorMessage.postValue(Event(ErrorResponse(404, it.message)))
                    }
                }
            }
        }
    }

    val spinnerItems = listOf("English", "Chinese - 中文")
}