package com.example.mod1c2.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("generate")
    fun generateText(
        @Body request: GenerateRequest
    ): Call<GenerateResponse>
}