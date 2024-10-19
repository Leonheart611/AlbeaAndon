package com.mika.enterprise.albeaandon.ui.finalize

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mika.enterprise.albeaandon.core.model.response.TicketData
import com.mika.enterprise.albeaandon.core.repository.NetworkRepository
import com.mika.enterprise.albeaandon.core.util.ErrorResponse
import com.mika.enterprise.albeaandon.core.util.ResultResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinalizeViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel() {

    private val _ticketDetail = MutableLiveData<TicketData>()
    val ticketDetail: LiveData<TicketData> = _ticketDetail
    private val _doneTicketStatus = MutableLiveData<Boolean>()
    val doneTicketStatus: LiveData<Boolean> = _doneTicketStatus
    private val _helpTicketStatus = MutableLiveData<Boolean>()
    val helpTicketStatus: LiveData<Boolean> = _helpTicketStatus
    private val _escalateTicketStatus = MutableLiveData<Boolean>()
    val escalateTicketStatus: LiveData<Boolean> = _escalateTicketStatus

    private val _errorTicket = MutableLiveData<ErrorResponse>()
    val errorTicket: LiveData<ErrorResponse> = _errorTicket

    val showLoading = MutableLiveData<Boolean>()
    val isUnauthorized = MutableLiveData<Boolean>()
    var ticketId = 0

    fun getTicketId(id: Int) {
        viewModelScope.launch {
            showLoading.postValue(true)
            repository.getTicketDetail(id).collect {
                showLoading.postValue(false)
                when (it) {
                    is ResultResponse.EmptyOrNotFound -> {}
                    is ResultResponse.Error -> {}
                    is ResultResponse.Success -> _ticketDetail.postValue(it.data.data.first())
                    is ResultResponse.UnAuthorized -> logout()
                }
            }
        }
    }

    fun doneTicket() {
        viewModelScope.launch {
            showLoading.postValue(true)
            repository.postNotifyTicket(isDone = 1, isHelp = 0, ticketId = ticketId).collect {
                showLoading.postValue(false)
                when (it) {
                    is ResultResponse.EmptyOrNotFound -> {}
                    is ResultResponse.Error -> { _errorTicket.postValue(it.errorResponse) }
                    is ResultResponse.Success -> _doneTicketStatus.postValue(true)
                    is ResultResponse.UnAuthorized -> logout()
                }
            }
        }
    }

    fun helpTicket() {
        viewModelScope.launch {
            showLoading.postValue(true)
            repository.postNotifyTicket(isHelp = 1, isDone = 0, ticketId = ticketId).collect {
                showLoading.postValue(false)
                when (it) {
                    is ResultResponse.EmptyOrNotFound -> {}
                    is ResultResponse.Error -> {}
                    is ResultResponse.Success -> _helpTicketStatus.postValue(true)
                    is ResultResponse.UnAuthorized -> logout()
                }
            }
        }
    }

    fun escalateTicket(ticketId: Int, message: String) {
        viewModelScope.launch {
            showLoading.postValue(true)
            repository.postEscalateTicket(ticketId = ticketId, message = message).collect {
                showLoading.postValue(false)
                when (it) {
                    is ResultResponse.EmptyOrNotFound -> {}
                    is ResultResponse.Error -> {}
                    is ResultResponse.Success -> _escalateTicketStatus.postValue(true)
                    is ResultResponse.UnAuthorized -> logout()
                }
            }
        }
    }

    fun logout() {
        isUnauthorized.postValue(true)
        viewModelScope.launch { repository.logout() }
    }
}