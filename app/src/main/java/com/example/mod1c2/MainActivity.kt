package com.example.mod1c2

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.mod1c2.network.ApiClient
import com.example.mod1c2.network.GenerateRequest
import com.example.mod1c2.network.GenerateResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : Activity() {

    private lateinit var edtPrompt: EditText
    private lateinit var spinnerModel: Spinner
    private lateinit var btnEvaluate: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var txtResult: TextView
    private lateinit var txtMetrics: TextView

    private val apiService = ApiClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtPrompt = findViewById(R.id.edtPrompt)
        spinnerModel = findViewById(R.id.spinnerModel)
        btnEvaluate = findViewById(R.id.btnEvaluate)
        progressBar = findViewById(R.id.progressBar)

        // Add result views programmatically
        val root = progressBar.parent as android.widget.LinearLayout

        txtResult = TextView(this).apply {
            textSize = 17f
            setTextColor(android.graphics.Color.BLACK)
            setPadding(0, 24, 0, 12)
            text = "Generated output will appear here."
        }

        txtMetrics = TextView(this).apply {
            textSize = 15f
            setTextColor(android.graphics.Color.DKGRAY)
            setPadding(0, 12, 0, 24)
        }

        root.addView(txtResult)
        root.addView(txtMetrics)

        progressBar.visibility = View.GONE

        val models = listOf(
            "Default Model",
            "Model 1",
            "Model 2"
        )

        spinnerModel.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            models
        )

        btnEvaluate.setOnClickListener {
            val prompt = edtPrompt.text.toString().trim()

            if (prompt.isEmpty()) {
                edtPrompt.error = "Please enter a prompt"
                return@setOnClickListener
            }

            evaluatePrompt(prompt)
        }
    }

    private fun evaluatePrompt(prompt: String) {
        btnEvaluate.isEnabled = false
        progressBar.visibility = View.VISIBLE
        txtResult.text = "Evaluating..."
        txtMetrics.text = ""

        val request = GenerateRequest(prompt = prompt)

        apiService.generateText(request)
            .enqueue(object : Callback<GenerateResponse> {

                override fun onResponse(
                    call: Call<GenerateResponse>,
                    response: Response<GenerateResponse>
                ) {
                    progressBar.visibility = View.GONE
                    btnEvaluate.isEnabled = true

                    if (response.isSuccessful) {
                        val result = response.body()

                        if (result != null) {
                            if (result.error != null) {
                                txtResult.text = "Error: ${result.error}"
                                return
                            }

                            txtResult.text =
                                result.generatedText
                                    ?: "No generated text received."

                            txtMetrics.text = """
                                Evaluation Metrics

                                Accuracy: ${result.accuracy ?: "N/A"}
                                Coherence: ${result.coherence ?: "N/A"}
                                Perplexity: ${result.perplexity ?: "N/A"}
                                BLEU: ${result.bleu ?: "N/A"}
                                ROUGE: ${result.rouge ?: "N/A"}
                            """.trimIndent()
                        } else {
                            txtResult.text = "Empty response from server."
                        }
                    } else {
                        txtResult.text =
                            "HTTP Error: ${response.code()}"
                        txtMetrics.text =
                            response.errorBody()?.string().orEmpty()
                    }
                }

                override fun onFailure(
                    call: Call<GenerateResponse>,
                    t: Throwable
                ) {
                    progressBar.visibility = View.GONE
                    btnEvaluate.isEnabled = true

                    txtResult.text =
                        "Connection failed: ${t.localizedMessage ?: "Unknown error"}"

                    txtMetrics.text =
                        "Check that your Python backend is running."
                }
            })
    }
}