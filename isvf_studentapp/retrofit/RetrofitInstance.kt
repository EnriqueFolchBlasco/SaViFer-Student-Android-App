package com.example.weatherapp.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object {
        private const val URL_BASE = "https://api.openweathermap.org/"

        private var retrofit: Retrofit? = null

        fun getInstance(): Retrofit {
            synchronized(this) {
                if (retrofit == null) {
                    retrofit = Retrofit.Builder()
                        .baseUrl(URL_BASE)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
                return retrofit!!
            }
        }
    }
}
