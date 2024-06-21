package com.presagetech.smartspectra_example


import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
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
import com.presagetech.smartspectra.SmartSpectraResultListener
import com.presagetech.smartspectra.SmartSpectraResultView
import com.presagetech.smartspectra.ScreeningResult
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    private lateinit var smartSpectraButton: SmartSpectraButton
    private lateinit var resultView: SmartSpectraResultView
    private lateinit var chartPulsePleth: LineChart
    private lateinit var chartBreathingPleth: LineChart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setting up SmartSpectra Results/Views
        smartSpectraButton = findViewById(R.id.btn)
        resultView = findViewById(R.id.result_view)
        chartPulsePleth = findViewById(R.id.chart_pulsePleth)
        chartBreathingPleth = findViewById(R.id.chart_breathingPleth)

        smartSpectraButton.setResultListener(resultListener)

        // Your api token from https://physiology.presagetech.com/
        smartSpectraButton.setApiKey("YOUR_API_KEY")

        // In case of unsupported devices
        if (!isSupportedAbi()) {
            smartSpectraButton.isEnabled = false
            Toast.makeText(this, "Unsupported device (ABI)", Toast.LENGTH_LONG).show()
            Timber.d("Unsupported device (ABI)")
            Timber.d("This device ABIs: ${Build.SUPPORTED_ABIS.contentToString()}")
        }
    }

    private val resultListener: SmartSpectraResultListener = SmartSpectraResultListener { result ->
        resultView.onResult(result) // pass the result to the view or handle it as needed
        // example usage of pulse and breathing pleth data (if present) to plot the pleth charts
        if (result is ScreeningResult.Success && !result.pulsePleth.isNullOrEmpty()) {
            chartPulsePleth.visibility = View.VISIBLE
            dataPlotting(chartPulsePleth, result.pulsePleth!!.map { Entry(it.time, it.value) })
        }
        if (result is ScreeningResult.Success && !result.breathingPleth.isNullOrEmpty()) {
            chartBreathingPleth.visibility = View.VISIBLE
            dataPlotting(chartBreathingPleth, result.breathingPleth!!.map { Entry(it.time, it.value) })
        }
    }

    private fun isSupportedAbi(): Boolean {
        Build.SUPPORTED_ABIS.forEach {
            if (it == "arm64-v8a" || it == "armeabi-v7a") {
                return true
            }
        }
        return false
    }

    /**
     * Configures and displays a line chart with the provided data entries.
     * This function sets up the line chart to show a simplified and clean visualization,
     * removing unnecessary visual elements like grid lines, axis lines, labels, and legends.
     * It sets the line color to red and ensures that no markers or value texts are shown.
     *
     * @param chart The LineChart object to configure and display data on.
     * @param entries The list of Entry objects representing the data points to be plotted.
     */
    private fun dataPlotting(chart: LineChart, entries: List<Entry>) {
        val dataSet = LineDataSet(entries, "Data")

        // Clean up line
        dataSet.setDrawValues(false)
        dataSet.setDrawCircles(false)
        dataSet.color = Color.RED
        val lineData = LineData(dataSet)

        // Clean up chart
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
        chart.onTouchListener = null

        chart.data = lineData
        chart.invalidate()
    }
}