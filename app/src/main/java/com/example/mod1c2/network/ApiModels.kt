package com.example.mod1c2.network
data class GenerateRequest(
    val prompt: String
)

data class GenerateResponse(
    val generatedText: String? = null,
    val accuracy: Double? = null,
    val coherence: Double? = null,
    val perplexity: Double? = null,
    val bleu: Double? = null,
    val rouge: Double? = null,
    val error: String? = null
)