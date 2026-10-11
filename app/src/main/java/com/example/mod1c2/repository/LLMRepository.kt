package com.example.mod1c2

import com.example.mod1c2.network.ApiClient
import com.example.mod1c2.network.GenerateRequest
import com.example.mod1c2.network.GenerateResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LLMRepository {

    private val apiService = ApiClient.apiService

    fun evaluatePrompt(
        prompt: String,
        onSuccess: (GenerateResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        if (prompt.isBlank()) {
            onError("Please enter a prompt.")
            return
        }

        val request = GenerateRequest(prompt = prompt)

        apiService.generateText(request)
            .enqueue(object : Callback<GenerateResponse> {

                override fun onResponse(
                    call: Call<GenerateResponse>,
                    response: Response<GenerateResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()

                        if (result != null) {
                            onSuccess(result)
                        } else {
                            onError("The server returned an empty response.")
                        }
                    } else {
                        onError("Server error: HTTP ${response.code()}")
                    }
                }

                override fun onFailure(
                    call: Call<GenerateResponse>,
                    t: Throwable
                ) {
                    onError(
                        t.localizedMessage ?: "Unable to connect to the backend."
                    )
                }
            })
    }
}
