package com.example.tryingsomething.service

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.tryingsomething.RetrofitClientService
import com.example.tryingsomething.dao.IRunnerDAO
import com.example.tryingsomething.dto.Runner
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class RunnerService {

    fun fetchRunners(runnerName: String) : MutableLiveData<ArrayList<Runner>> {
        var _runners =  MutableLiveData<ArrayList<Runner>>()

        val service = RetrofitClientService.retrofitInstance?.create(IRunnerDAO::class.java)
        val call = service?.getAllRunners()
        call?.enqueue(object : retrofit2.Callback<ArrayList<Runner>>{
            override fun onResponse(call: Call<ArrayList<Runner>>, response: Response<ArrayList<Runner>>) {
                _runners.value = response.body()
            }
            override fun onFailure(call: Call<ArrayList<Runner>>, t: Throwable) {
                val j = 1+1
                Log.i("Retro", t.toString())
            }
        })
        return _runners
    }
}