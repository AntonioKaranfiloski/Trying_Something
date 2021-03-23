package com.example.tryingsomething

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientService {

    private var retrofit: Retrofit? = null
    private val BASE_URL = "https://www.plantplaces.com"

    val retrofitInstance: Retrofit?
        get() {
            //has it be creatated jet?
            if (retrofit == null){
                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return retrofit
        }
}