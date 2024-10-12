package com.mika.enterprise.albeaandon.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mika.enterprise.albeaandon.core.model.response.TicketData
import com.mika.enterprise.albeaandon.core.repository.NetworkRepository
import com.mika.enterprise.albeaandon.core.repository.UserRepository
import com.mika.enterprise.albeaandon.core.util.Constant.ASSIGNED
import com.mika.enterprise.albeaandon.core.util.Constant.ESKALASI
import com.mika.enterprise.albeaandon.core.util.Constant.MECHANIC
import com.mika.enterprise.albeaandon.core.util.Constant.NEW
import com.mika.enterprise.albeaandon.core.util.Constant.ONPROG
import com.mika.enterprise.albeaandon.core.util.Constant.OPERATOR_BAHAN
import com.mika.enterprise.albeaandon.core.util.Constant.SPV_PRODUCTION
import com.mika.enterprise.albeaandon.core.util.ResultResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _ticketResult = MutableLiveData<List<TicketData>>()
    val ticketResult: LiveData<List<TicketData>> = _ticketResult

    val username = MutableLiveData<String>()
    val showLoading = MutableLiveData<Boolean>()
    val showEmptyState = MutableLiveData<Boolean>()
    val isNotAuthorized = MutableLiveData<Boolean>()
    val pageNext = MutableLiveData<Boolean>()

    var currentPage = 1
    var maxPage = 0
    var jobPosition = ""

    fun getUserName() {
        viewModelScope.launch { username.postValue(userRepository.login()?.username) }
    }

    fun getTicketList() {
        showLoading.postValue(true)
        viewModelScope.launch {
            networkRepository.getTickets(
                getFilterMapping(userRepository.login()?.userDept.orEmpty()).joinToString(","),
                currentPage
            ).collect {
                showLoading.postValue(false)
                when (it) {
                    is ResultResponse.Error -> {
                        FirebaseCrashlytics.getInstance()
                            .recordException(it.exception ?: Exception("Unknown error"))
                    }

                    is ResultResponse.Success -> {
                        if (it.data.data.isEmpty()) showEmptyState.postValue(true)
                        else {
                            showEmptyState.postValue(false)
                            _ticketResult.value = it.data.data
                            currentPage = currentPage++
                            if (maxPage == 0) {
                                val totalPage = it.data.page.total / it.data.page.limit
                                maxPage = ceil(totalPage.toDouble()).toInt()
                            }
                            pageNext.postValue(currentPage < maxPage)
                        }

                    }

                    is ResultResponse.UnAuthorized -> isNotAuthorized.postValue(true)
                }
            }
        }
    }


    private fun getFilterMapping(jobPosition: String): List<String> {
        this.jobPosition = jobPosition
        return when (jobPosition) {
            SPV_PRODUCTION -> listOf(ONPROG, NEW, ESKALASI)
            MECHANIC, OPERATOR_BAHAN -> listOf(ASSIGNED, ONPROG)
            else -> listOf()
        }
    }


    fun logout() {
        viewModelScope.launch { networkRepository.logout() }
    }

    fun loadNextPage() = pageNext.switchMap {
        if (it) getTicketList()
        return@switchMap ticketResult
    }

}