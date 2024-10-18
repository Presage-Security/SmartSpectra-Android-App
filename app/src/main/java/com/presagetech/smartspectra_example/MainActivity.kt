package com.presagetech.smartspectra_example


import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// Plotting imports
import androidx.core.view.isVisible
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.presage.physiology.proto.MetricsProto.MetricsBuffer

// SmartSpectra SDK Specific Imports
import com.presagetech.smartspectra.SmartSpectraView


class MainActivity : AppCompatActivity() {
    private lateinit var smartSpectraView: SmartSpectraView
    private lateinit var chartContainer: LinearLayout
    private lateinit var faceMeshContainer: ScatterChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setting up SmartSpectra Results/Views
        smartSpectraView = findViewById(R.id.smart_spectra_view)
        // setup views for plots
        chartContainer = findViewById(R.id.chart_container)
        faceMeshContainer = findViewById(R.id.mesh_container)

        //Required configuration
        // Your api token from https://physiology.presagetech.com/
        smartSpectraView.setApiKey("YOUR_API_KEY")

        // Optional configurations
        // Valid range for spot time is between 20.0 and 120.0
        smartSpectraView.setSpotTime(30.0)
        smartSpectraView.setShowFps(false)
        //Recording delay defaults to 3 if not provided
        smartSpectraView.setRecordingDelay(3)

        // Optional: Only need to set it if you want to access face mesh points
        smartSpectraView.setMeshPointsObserver{ meshPoints ->
            handleMeshPoints(meshPoints)
        }

        // Optional: Only need to set it if you want to access metrics to do any processing
        smartSpectraView.setMetricsBufferObserver { metricsBuffer ->
            handleMetricsBuffer(metricsBuffer)
        }
    }

    private fun handleMeshPoints(meshPoints: List<Pair<Int, Int>>) {
        // TODO: Update UI or handle the points as needed

        // Reference the ScatterChart from the layout
        val chart = faceMeshContainer
        chart.isVisible = true


        // Scale the points and sort by x
        // Sorting is important here for scatter plot as unsorted points cause negative array size exception in scatter chart
        // See https://github.com/PhilJay/MPAndroidChart/issues/2074#issuecomment-239936758
        // --- Important --- we are subtracting the y points for plotting since (0,0) is top-left on the screen but bottom-left on the chart
        // --- Important --- we are subtracting the x points to mirror horizontally
        val scaledPoints = meshPoints.map { Entry(1f - it.first / 720f, 1f - it.second / 720f) }
            .sortedBy { it.x }

        // Create a dataset and add the scaled points
        val dataSet = ScatterDataSet(scaledPoints, "Mesh Points").apply {
            setDrawValues(false)
            scatterShapeSize = 15f
            setScatterShape(ScatterChart.ScatterShape.CIRCLE)
        }

        // Create ScatterData with the dataset
        val scatterData = ScatterData(dataSet)

        // Customize the chart
        chart.apply {
            data = scatterData
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            xAxis.isEnabled = false
            setTouchEnabled(false)
            description.isEnabled = false
            legend.isEnabled = false

            // Set visible range to make x and y axis have the same range

            setVisibleXRange(0f, 1f)
            setVisibleYRange(0f, 1f, YAxis.AxisDependency.LEFT)

            // Move view to the data
            moveViewTo(0f, 0f, YAxis.AxisDependency.LEFT)
        }

        // Refresh the chart
        chart.invalidate()
    }

    private fun handleMetricsBuffer(metrics: MetricsBuffer) {
        // Clear the chart container before plotting new results
        chartContainer.removeAllViews()

        // get the relevant metrics
        val pulse = metrics.pulse
        val breathing = metrics.breathing
        val bloodPressure = metrics.bloodPressure
        val face = metrics.face

        // Plot the results

        // Pulse plots
        if (pulse.traceCount > 0) {
            addChart(pulse.traceList.map { Entry(it.time, it.value) },  "Pulse Pleth", false)
        }
        if (pulse.rateCount > 0) {
            addChart( pulse.rateList.map { Entry(it.time, it.value) }, "Pulse Rates", true)
            addChart( pulse.rateList.map { Entry(it.time, it.confidence) }, "Pulse Rate Confidence", true)

        }
        //TODO: 9/30/24: add this chart when hrv is added to protobuf
//        addChart( pulse..hrv.map { Entry(it.time, it.value) }, "Pulse Rate Variability", true)

        // Breathing plots
        if (breathing.upperTraceCount > 0) {
            addChart(breathing.upperTraceList.map { Entry(it.time, it.value) }, "Breathing Pleth", false)
        }
        if (breathing.rateCount > 0) {
            addChart(breathing.rateList.map { Entry(it.time, it.value) }, "Breathing Rates", true)
            addChart(breathing.rateList.map { Entry(it.time, it.confidence) }, "Breathing Rate Confidence", true)
        }
        if (breathing.amplitudeCount > 0) {
            addChart(breathing.amplitudeList.map { Entry(it.time, it.value) }, "Breathing Amplitude", true)
        }
        if (breathing.apneaCount > 0) {
            addChart(breathing.apneaList.map { Entry(it.time, if (it.detected) 1f else 0f) }, "Apnea", true)
        }
        if (breathing.baselineCount > 0) {
            addChart(breathing.baselineList.map { Entry(it.time, it.value) }, "Breathing Baseline", true)
        }
        if (breathing.respiratoryLineLengthCount > 0) {
            addChart(breathing.respiratoryLineLengthList.map { Entry(it.time, it.value) }, "Respiratory Line Length", true)
        }
        if (breathing.inhaleExhaleRatioCount > 0) {
            addChart(
                breathing.inhaleExhaleRatioList.map { Entry(it.time, it.value) },
                "Inhale-Exhale Ratio",
                true
            )
        }

        // Blood pressure plots
        if (bloodPressure.phasicCount > 0) {
            addChart(bloodPressure.phasicList.map { Entry(it.time, it.value) }, "Phasic", true)
        }

        // Face plots
        if (face.blinkingCount > 0) {
            addChart(face.blinkingList.map { Entry(it.time, if (it.detected) 1f else 0f) }, "Blinking", true)
        }
        if (face.talkingCount > 0) {
            addChart(face.talkingList.map { Entry(it.time, if (it.detected) 1f else 0f) }, "Talking", true)
        }

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
