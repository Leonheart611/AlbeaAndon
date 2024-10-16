package com.mika.enterprise.albeaandon.ui.progress

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mika.enterprise.albeaandon.core.model.response.ProblemGroupResponse
import com.mika.enterprise.albeaandon.core.model.response.toGeneralProblem
import com.mika.enterprise.albeaandon.core.repository.NetworkRepository
import com.mika.enterprise.albeaandon.core.util.ResultResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {

    val showLoading = MutableLiveData<Boolean>()

    private val _problemGroupResponse = MutableLiveData<ProblemGroupResponse>()
    val problemGroupResponse: LiveData<ProblemGroupResponse> = _problemGroupResponse

    val isUnAuthorized = MutableLiveData<Boolean>()

    fun getProblemGroup() {
        viewModelScope.launch {
            showLoading.postValue(true)
            repository.getProblemGroup().collect {
                showLoading.postValue(false)
                when (it) {
                    is ResultResponse.Success -> {
                        _problemGroupResponse.postValue(it.data)
                    }

                    is ResultResponse.Error -> {}
                    is ResultResponse.UnAuthorized -> isUnAuthorized.postValue(true)
                }
            }
        }
    }

    fun getProblem(id: Int) {
        viewModelScope.launch {
            showLoading.postValue(true)
            repository.getProblem(id).collect {
                showLoading.postValue(false)
                when (it) {
                    is ResultResponse.Success -> {
                        _problemGroupResponse.postValue(it.data)
                    }

                    is ResultResponse.Error -> {}
                    is ResultResponse.UnAuthorized -> {}
                }

            }
        }
    }

    fun getTodoProblem(id: Int) {
        viewModelScope.launch {
            showLoading.postValue(true)
            repository.getTodoProblem(id).collect {
                showLoading.postValue(false)
                when (it) {
                    is ResultResponse.Success -> {
                        _problemGroupResponse.postValue(
                            ProblemGroupResponse(
                                it.data.todoProblem.toGeneralProblem(),
                                success = true
                            )
                        )
                    }

                    is ResultResponse.Error -> {}
                    is ResultResponse.UnAuthorized -> {}
                }
            }
        }
    }

    fun logOut() {
        viewModelScope.launch { repository.logout() }
    }

}