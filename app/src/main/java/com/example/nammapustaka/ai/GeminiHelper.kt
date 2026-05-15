package com.example.nammapustaka.ai

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiHelper(private val apiKey: String) {
    
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    suspend fun getKannadaSummary(bookTitle: String, author: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = "Provide a concise 2-sentence summary of the book '$bookTitle' by '$author' in Kannada language suitable for primary school children."
                val response = generativeModel.generateContent(prompt)
                response.text ?: "Summary not available"
            } catch (e: Exception) {
                "Error generating summary: ${e.message}"
            }
        }
    }
}
