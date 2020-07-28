package com.anywhereapps.yelp.test.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitBuilder {

    private const val BASE_URL1 = "https://api.yelp.com/"
    const val TOKEN = "Bearer zEAl9ALZtSge9s_sbLcgGNJ1ahQjM-YAbPAfGdv4bQF_FMWWzzvulRqlCeswQ21gBTq3p2vvJixx-OeNDRywKdira05TzADA2RZ4XWINLUlUetrSxR3icuaow1IfX3Yx"


    private fun getRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL1)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService = getRetrofit()
        .create(ApiService::class.java)
}