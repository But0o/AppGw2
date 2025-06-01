package com.example.gw2.data

import com.example.gw2.data.api.IGw2Api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: IGw2Api by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.guildwars2.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IGw2Api::class.java)
    }
}
