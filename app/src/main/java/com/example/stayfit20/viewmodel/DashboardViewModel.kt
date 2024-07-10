package com.example.stayfit20.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _currentFragmentTag = MutableLiveData<String>()
    val currentFragmentTag: LiveData<String>
        get() = _currentFragmentTag

    fun setCurrentFragmentTag(tag: String) {
        _currentFragmentTag.value = tag
    }
}


