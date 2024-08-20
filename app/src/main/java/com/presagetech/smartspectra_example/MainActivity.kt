package com.presagetech.smartspectra_example


import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// Plotting imports
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

// SmartSpectra SDK Specific Imports
import com.presagetech.smartspectra.ScreeningResult
import com.presagetech.smartspectra.SmartSpectraButton
import com.presagetech.smartspectra.SmartSpectraResultListener
import com.presagetech.smartspectra.SmartSpectraResultView
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    private lateinit var smartSpectraButton: SmartSpectraButton
    private lateinit var resultView: SmartSpectraResultView
    private lateinit var chartContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setting up SmartSpectra Results/Views
        smartSpectraButton = findViewById(R.id.btn)
        resultView = findViewById(R.id.result_view)
        chartContainer = findViewById(R.id.chart_container)

        smartSpectraButton.setResultListener(resultListener)
        // Valid range for spot time is between 20.0 and 120.0
        smartSpectraButton.setSpotTime(30.0)
        smartSpectraButton.setShowFps(false)

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
        resultView.onResult(result) // pass the result to the sdk's result view or handle it as needed

        // Clear the chart container before plotting new results
        chartContainer.removeAllViews()

        // Plot the results
        if (result is ScreeningResult.Success) {
            result.pulsePleth?.let {
                addChart( it.map { Entry(it.time, it.value) }, "Pulse Pleth", false)
            }
            result.breathingPleth?.let {
                addChart( it.map { Entry(it.time, it.value) }, "Breathing Pleth", false)
            }
            result.pulseValues?.let {
                addChart( it.map { Entry(it.time, it.value) }, "Pulse Rates", true)
            }
            result.pulseConfidence?.let {
                addChart( it.map { Entry(it.time, it.value) }, "Pulse Rate Confidence", true)
            }
            result.hrv?.let {
                addChart( it.map { Entry(it.time, it.value) }, "Pulse Rate Variability", true)
            }

            result.breathingValues?.let {
                addChart( it.map { Entry(it.time, it.value) }, "Breathing Rates", true)
            }
            result.breathingConfidence?.let {
                addChart( it.map { Entry(it.time, it.value) }, "Breathing Rate Confidence", true)
            }
            result.breathingAmplitude?.let {
                addChart( it.map { Entry(it.time, it.value) }, "Breathing Amplitude", true)
            }
            result.apnea?.let {
                addChart( it.map { Entry(it.time, if(it.value) 1f else 0f) }, "Apnea", true)
            }
            result.breathingBaseline?.let {
                addChart( it.map { Entry(it.time, it.value) }, "Breathing Baseline", true)
            }
            result.phasic?.let {
                addChart( it.map { Entry(it.time, it.value) }, "Phasic", true)
            }
            result.rrl?.let {
                addChart( it.map { Entry(it.time, it.value) }, "RRL", true)
            }
            result.ie?.let {
                addChart( it.map { Entry(it.time, it.value) }, "IE", true)
            }
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

    private fun addChart(entries: List<Entry>, title: String, showYTicks: Boolean) {
        val chart = LineChart(this)

        val density = resources.displayMetrics.density
        val heightInPx = (200 * density).toInt()

        chart.layoutParams = LinearLayout.LayoutParams (
            LinearLayout.LayoutParams.MATCH_PARENT,
            heightInPx
        )


        val titleView = TextView(this)
        titleView.text = title
        titleView.textSize = 18f
        titleView.gravity = Gravity.CENTER
        titleView.setTypeface(null, Typeface.BOLD)

        val xLabelView = TextView(this)
        xLabelView.setText(R.string.api_xLabel)
        xLabelView.gravity = Gravity.CENTER
        xLabelView.setPadding(0, 0, 0, 20)

        chartContainer.addView(titleView)
        chartContainer.addView(chart)
        chartContainer.addView(xLabelView)

        dataPlotting(chart, entries, showYTicks)
    }

    /**
     * Configures and displays a line chart with the provided data entries.
     * This function sets up the line chart to show a simplified and clean visualization,
     * removing unnecessary visual elements like grid lines, axis lines, labels, and legends.
     * It sets the line color to red and ensures that no markers or value texts are shown.
     *
     * @param chart The LineChart object to configure and display data on.
     * @param entries The list of Entry objects representing the data points to be plotted.
     * @param showYTicks Whether to show the Y axis ticks
     */
    private fun dataPlotting(chart: LineChart, entries: List<Entry>, showYTicks: Boolean) {
        val dataSet = LineDataSet(entries, "Data")

        // Clean up line
        dataSet.setDrawValues(false)
        dataSet.setDrawCircles(false)
        dataSet.color = Color.RED

        chart.data = LineData(dataSet)

        // x axis setup
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.setDrawAxisLine(true)
        chart.xAxis.granularity = 1.0f


        // y axis setup
        chart.axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        chart.axisLeft.setDrawZeroLine(false)
        chart.axisLeft.setDrawGridLines(false)
        chart.axisLeft.setDrawAxisLine(true)
        chart.axisLeft.setDrawLabels(showYTicks)

        // chart specific setup
        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
        chart.onTouchListener = null
        chart.invalidate()

    }

}