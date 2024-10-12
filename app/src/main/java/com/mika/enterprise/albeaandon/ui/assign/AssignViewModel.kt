package com.mika.enterprise.albeaandon.ui.assign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mika.enterprise.albeaandon.core.model.response.PersonnelData
import com.mika.enterprise.albeaandon.core.repository.NetworkRepository
import com.mika.enterprise.albeaandon.core.util.ResultResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignViewModel @Inject constructor(
    val networkRepository: NetworkRepository
) : ViewModel() {

    val showLoading = MutableLiveData<Boolean>()
    val isNotAuthorized = MutableLiveData<Boolean>()
    private val _personnelList = MutableLiveData<List<PersonnelData>>()
    val personnelList: LiveData<List<PersonnelData>> = _personnelList

    var selectedPersonnel: PersonnelData? = null
    var isNotSameUserGroup = false
    private var currentUserGroup = "A"

    fun getPersonnelList(userGroup: String) {
        viewModelScope.launch {
            showLoading.postValue(true)
            isNotSameUserGroup = currentUserGroup != userGroup
            networkRepository.getPersonnelsAvailability(userGroup).collect {
                showLoading.postValue(false)
                when (it) {
                    is ResultResponse.Error -> {

                    }

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

}