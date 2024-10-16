package com.mika.enterprise.albeaandon.ui.assign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mika.enterprise.albeaandon.core.model.response.PersonnelData
import com.mika.enterprise.albeaandon.core.repository.NetworkRepository
import com.mika.enterprise.albeaandon.core.repository.UserRepository
import com.mika.enterprise.albeaandon.core.util.ResultResponse
import com.mika.enterprise.albeaandon.core.util.mappingAssignFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignViewModel @Inject constructor(
    val networkRepository: NetworkRepository,
    val userRepository: UserRepository
) : ViewModel() {

    val showLoading = MutableLiveData<Boolean>()
    val isNotAuthorized = MutableLiveData<Boolean>()
    private val _personnelList = MutableLiveData<List<PersonnelData>>()
    val personnelList: LiveData<List<PersonnelData>> = _personnelList
    private val _assignResult = MutableLiveData<Boolean>()
    val assignResult: LiveData<Boolean> = _assignResult

    var selectedPersonnel: PersonnelData? = null
    var isNotSameUserGroup = false
    private var currentUserGroup = "A"

    fun getPersonnelList(userGroup: String) {
        viewModelScope.launch {
            val userDept = userRepository.login()?.userDept.orEmpty()
            showLoading.postValue(true)
            isNotSameUserGroup = currentUserGroup != userGroup
            networkRepository.getPersonnelsAvailability(userGroup, mappingAssignFilter(userDept))
                .collect {
                    showLoading.postValue(false)
                    when (it) {
                        is ResultResponse.Error -> {}
                        is ResultResponse.Success -> {
                            if (isNotSameUserGroup) selectedPersonnel = null
                            _personnelList.postValue(it.data.data)
                            currentUserGroup = userGroup
                        }

                        is ResultResponse.UnAuthorized -> {
                            isNotAuthorized.postValue(true)
                        }
                    }
                }
        }
    }

    fun postAssignTicket(username: String, ticketId: Int) {
        viewModelScope.launch {
            showLoading.postValue(true)
            networkRepository.postAssignTicket(username, ticketId).collect {
                showLoading.postValue(false)
                when (it) {
                    is ResultResponse.Error -> {

                    }

                    is ResultResponse.Success -> {
                        _assignResult.postValue(it.data.success)
                    }

                    is ResultResponse.UnAuthorized -> {
                        isNotAuthorized.postValue(true)
                    }
                }
            }

        }
    }

    fun logout() {
        viewModelScope.launch { networkRepository.logout() }
    }
}