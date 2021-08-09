package com.arpit.newsapp3

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

const val base_url = "https://newsapi.org/"
const val api_key = "4bd47cc79f5146dfae3acfbc16c9b011"


interface APIService {
    @GET("v2/top-headlines?country=in&apiKey=$api_key")

    fun getHeadlines() : Call<News>



    //https://newsapi.org/v2/top-headlines?apiKey=$api_key&country=in&page=2
}

object  newsService {
    val apiService : APIService
//    val apiService : APIService
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(APIService::class.java)
    }
}



