package com.example.tryingsomething.service

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.tryingsomething.RetrofitClientService
import com.example.tryingsomething.dao.IRunnerDAO
import com.example.tryingsomething.dto.Another
import retrofit2.Call
import retrofit2.Response

class RunnerService {

    fun fetchRunners(runnerName: String) : MutableLiveData<ArrayList<Another>> {
        var _runners =  MutableLiveData<ArrayList<Another>>()

        val service = RetrofitClientService.retrofitInstance?.create(IRunnerDAO::class.java)
        val call = service?.getAllRunners()
        call?.enqueue(object : retrofit2.Callback<ArrayList<Another>>{
            override fun onResponse(call: Call<ArrayList<Another>>, response: Response<ArrayList<Another>>) {
                _runners.value = response.body()
            }
            override fun onFailure(call: Call<ArrayList<Another>>, t: Throwable) {
                val j = 1+1
                Log.i("Retro", t.toString())
            }
        })
        return _runners
    }
}