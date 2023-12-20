package com.presagetech.smartspectra_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.presagetech.smartspectra.core.button.SmartSpectraButton
import com.presagetech.smartspectra.core.result.ScreeningResult
import com.presagetech.smartspectra.core.result.SmartSpectraResultView

class MainActivity : AppCompatActivity() {
    private lateinit var resultView: SmartSpectraResultView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultView = findViewById(R.id.result_view)

        val smartSpectraButton: SmartSpectraButton = findViewById(R.id.btn)
        smartSpectraButton.setResultListener(this::onSmartSpectraResult)

        smartSpectraButton.setApiKey("YOUR_API_KEY_HERE")
    }

    private fun onSmartSpectraResult(result: ScreeningResult) {
        // Handle measured values yourself
        when (result) {
            is ScreeningResult.Success -> {
                val averageHartRate = result.hrAverage.toInt()
                val averageRespirationRate = result.rrAverage.toInt()
                Toast.makeText(
                    this,
                    "Heart rate: $averageHartRate, Respiration rate: $averageRespirationRate",
                    Toast.LENGTH_SHORT
                ).show()
            }
            is ScreeningResult.Failed -> {
                Toast.makeText(this, "Measure failed", Toast.LENGTH_SHORT).show()
            }
        }

        // OR display it with default view
        resultView.onResult(result)
    }
}
