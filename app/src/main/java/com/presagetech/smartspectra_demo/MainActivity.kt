package com.presagetech.smartspectra_example


import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// Plotting imports
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

// SmartSpectra SDK Specific Imports
import com.presagetech.smartspectra.SmartSpectraButton
import com.presagetech.smartspectra.SmartSpectraResultView
import org.json.JSONObject
import timber.log.Timber


class MainActivity : AppCompatActivity(), SmartSpectraResultView.SmartSpectraResultsCallback {
    private lateinit var tokenEditText: EditText
    private lateinit var smartSpectraButton: SmartSpectraButton
    private lateinit var chart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setting up SmartSpectra Results/Views
        smartSpectraButton = findViewById(R.id.btn)
        val resultView = findViewById<SmartSpectraResultView>(R.id.result_view)
        resultView.callback = this
        smartSpectraButton.setResultListener(resultView)
        chart = findViewById(R.id.chart)


        // API Key Entry
        tokenEditText = findViewById(R.id.text_api_token)
        tokenEditText.setOnEditorActionListener { _, _, _ ->
            val token = tokenEditText.text.toString().trim()
            saveToken(token)
            smartSpectraButton.setApiKey(token)
            true
        }
        val storedToken = loadToken()
        smartSpectraButton.setApiKey("YOUR_API_KEY_HERE")
        tokenEditText.setText(storedToken)

        // In case of unsupported devices
        if (!isSupportedAbi()) {
            smartSpectraButton.isEnabled = false
            tokenEditText.isEnabled = false
            Toast.makeText(this, "Unsupported device (ABI)", Toast.LENGTH_LONG).show()
            Timber.d("Unsupported device (ABI)")
            Timber.d("This device ABIs: ${Build.SUPPORTED_ABIS.contentToString()}")
        }
    }

    /**
     * Receives JSON metrics from the measurement (see readme for JSON structure).
     * @param jsonMetrics The complete JSON object containing various metrics.
     */
    override fun onMetricsJsonReceive(jsonMetrics: JSONObject) {
        // Here you can handle the received Metrics JSON
        // Pulse Pleth Example
        // Extract Data
        val plethPulseJson = jsonMetrics.getJSONObject("pulse").getJSONObject("hr_trace")
        // Parse and Sort the data
        val plethEntries = parseAndSortEntries(plethPulseJson)
        // Plot the Data
        dataPlotting(plethEntries)
        Timber.d("Received JSON data: $jsonMetrics")
    }

    /**
     * Recieves the strict pulse rate from the measurement.
     * Strict pulse rates are the average of only high confidence pulse rate values.
     * @param strictPulseRate The strict pulse rate received, measured in beats per minute.
     */
    override fun onStrictPuleRateReceived(strictPulseRate: Int) {
        // Here you can handle the received strict Pulse Rate in beats per minute
        Timber.d("Received JSON data: $strictPulseRate")
    }

    /**
     * Recieves the strict breathing rate from the measurement.
     * Strict breathing rates are the average of only high confidence breathing rate values.
     * @param strictBreathingRate The strict breathing rate received, measured in beats per minute.
     */
    override fun onStrictBreathingRateReceived(strictBreathingRate: Int) {
        // Here you can handle the received strict Breathing Rate in beats per minute
        Timber.d("Received JSON data: $strictBreathingRate")
    }

    /**
     * Parses a JSON object containing time and values into a sorted list of entries.
     * @param json The JSON object where each key is a timestamp and each value is a nested JSON object with a "value" key.
     * @return A sorted list of entries for use in a chart.
     */
    fun parseAndSortEntries(json: JSONObject): List<Entry> {
        val entries = mutableListOf<Entry>()

        val iterator = json.keys()
        while (iterator.hasNext()) {
            val time = iterator.next() // This is the string key
            val value = json.getJSONObject(time).getDouble("value")
            entries.add(Entry(time.toFloat(), value.toFloat()))
        }

        // Sort the entries based on the X value (time)
        entries.sortBy { it.x }

        return entries
    }


    /**
     * Configures and displays a line chart with the provided data entries.
     * This function sets up the line chart to show a simplified and clean visualization,
     * removing unnecessary visual elements like grid lines, axis lines, labels, and legends.
     * It sets the line color to red and ensures that no markers or value texts are shown.
     *
     * @param entries The list of Entry objects representing the data points to be plotted.
     */
    private fun dataPlotting(entries: List<Entry>) {
        val dataSet = LineDataSet(entries, "Data")

        // Clean up line
        dataSet.setDrawValues(false)
        dataSet.setDrawCircles(false)
        dataSet.color = Color.RED
        val lineData = LineData(dataSet)

        // clean up chart
        val xAxis = chart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawLabels(false)
        val leftAxis = chart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawLabels(false)
        val rightAxis = chart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawLabels(false)
        chart.legend.isEnabled = false
        chart.description.isEnabled = false

        chart.data = lineData
        chart.invalidate() // refresh the chart
    }



    private fun isSupportedAbi(): Boolean {
        Build.SUPPORTED_ABIS.forEach {
            if (it == "arm64-v8a" || it == "armeabi-v7a") {
                return true
            }
        }
        return false
    }

    private fun loadToken(): String {
        return getSharedPreferences(TOKEN_SHARED_PREFERENCES, MODE_PRIVATE)
            .getString(TOKEN_SHARED_PREFERENCES, null) ?: ""
    }

    private fun saveToken(token: String) {
        getSharedPreferences(TOKEN_SHARED_PREFERENCES, MODE_PRIVATE).edit().apply {
            putString(TOKEN_SHARED_PREFERENCES, token)
            apply()
        }
    }

    companion object {
        const val TOKEN_SHARED_PREFERENCES = "demo_api_key"
    }
}
