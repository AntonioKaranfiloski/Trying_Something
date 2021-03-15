package com.example.tryingsomething.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tryingsomething.dto.Runner
import com.example.tryingsomething.service.RunnerService

class MainViewModel : ViewModel() {
    private var _runners: MutableLiveData<ArrayList<Runner>> = MutableLiveData<ArrayList<Runner>>()
    var runners: MutableLiveData<ArrayList<Runner>>
        get() {return _runners}
        set(value) { _runners = value}

    var runnerService: RunnerService = RunnerService()

    init {
        fetchRunners("e")
    }

    fun fetchRunners(runnerName: String) {
        _runners = runnerService.fetchRunners(runnerName)
    }
    // TODO: Implement the ViewModel
}