package com.example.exercise2

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import java.io.IOException

data class response(val result: result)
data class result(val label: String?)
data class GoogleSentimentPrediction(val content: Content?)
data class Content(val parts: List<Part>?)
data class Part(val text: String?)

object SentimentAnalyzer
{
    private val client = OkHttpClient()
    private const val API_URL = "http://10.0.2.2:5000/predict"

    fun analyzeSentiment(text: String, callback: (String) -> Unit)
    {
        val jsonRequest = createJsonRequest(text)
        val request = buildRequest(jsonRequest)
        executeRequest(request, callback)
    }

    private fun createJsonRequest(text: String): String
    {
        return """
            {
                "sentence": "$text"
            }
        """.trimIndent()
    }

    private fun buildRequest(jsonRequest: String): Request
    {
        return Request.Builder()
            .url(API_URL)
            .post(jsonRequest.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
    }

    private fun executeRequest(request: Request, callback: (String) -> Unit)
    {
        client.newCall(request).enqueue(object : Callback
        {
            override fun onFailure(call: Call, e: IOException)
            {
                callback("Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response)
            {
                response.body?.string()?.let { responseData ->
                    parseResponse(responseData, callback)
                } ?: callback("Error: Empty response")
            }
        })
    }

    private fun parseResponse(responseData: String, callback: (String) -> Unit)
    {
        try
        {
            val sentimentResponse = Gson().fromJson(responseData, response::class.java)
            val sentiment = sentimentResponse.result.label
            callback(sentiment ?: "Error: No sentiment detected")
        } catch (e: Exception)
        {
            callback("Error parsing response: ${e.message}")
        }
    }
}