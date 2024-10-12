package com.mika.enterprise.albeaandon

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mika.enterprise.albeaandon.core.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    val nfcValue = MutableLiveData<Event<String>>()
}