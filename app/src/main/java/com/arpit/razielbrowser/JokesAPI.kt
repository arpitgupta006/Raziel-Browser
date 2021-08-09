package com.arpit.razielbrowser

import com.arpit.newsapp3.base_url
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET

interface JokesAPI {
    @GET("joke/Any")

    fun getJokes() : Call<ResponseJokes>
}

object jokesService{

    val api : JokesAPI
    init {
              val retrofit = Retrofit.Builder()
                      .baseUrl("https://v2.jokeapi.dev/")
                      .addConverterFactory(GsonConverterFactory.create())
                      .build()
        api = retrofit.create(JokesAPI::class.java)
    }
}