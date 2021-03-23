package com.example.tryingsomething.dao

import com.example.tryingsomething.dto.Another
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IRunnerDAO {
    @GET("/perl/mobile/viewplantsjsonarray.pl")
    fun getAllRunners(): Call<ArrayList<Another>>

    @GET("/perl/mobile/viewplantsjsonarray.pl")
    fun getPlants(@Query("Combined_Name") runnerName: String): Call<ArrayList<Another>>
}