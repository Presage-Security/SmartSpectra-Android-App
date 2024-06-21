# Maven SmartSpectra SDK Integration Guide

This provides instructions for integrating and utilizing the Presage SmartSpectra SDK publicly hosted on Maven in your Android application to measure physiology metrics from a 30 second measurement using the mobile device's camera.

This app contained in this repo is an example of pulling and using the SmartSpectra SDK from Maven. It should run out of the box as long as your API key is provided.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Setup](#setup)
- [Usage](#usage)
- [API Key](#api-key)
- [Handling Results](#handling-results)
- [Troubleshooting](#troubleshooting)
- [Known Bugs](#known-bugs)


## Prerequisites
Before you start, ensure your development environment includes:
- Android Studio Giraffe or later
- Minimum SDK level 21

## Installation
To integrate the SmartSpectra SDK into your Android project, add the following dependency to your app's `build.gradle` file:

```gradle
dependencies {
    implementation 'com.presagetech:smartspectra:1.0.6'
}
```
While the sdk library is in development process it may be necessary to add `maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")`
to `settings.gradle.kt`, like this:
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}
```

## Setup
### Initialize Components
In your activity or fragment, initialize the SmartSpectraButton, SmartSpectraResultListener, SmartSpectraResultView, 
and ScreeningResult:
```kotlin
import com.presagetech.smartspectra.SmartSpectraButton
import com.presagetech.smartspectra.SmartSpectraResultListener
import com.presagetech.smartspectra.SmartSpectraResultView
import com.presagetech.smartspectra.ScreeningResult

 override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setting up SmartSpectra Results/Views
        smartSpectraButton = findViewById(R.id.btn)
        resultView = findViewById(R.id.result_view)
        
        smartSpectraButton.setResultListener(resultListener)
```
### Set API Key
You need a valid API key to authenticate your requests:
```kotlin
smartSpectraButton.setApiKey("YOUR_API_KEY")
```
You can obtain an API key from PresageTech's developer portal (https://physiology.presagetech.com/)

## Usage
### Example Code
Please refer to [MainActivity.kt](app/src/main/java/com/presagetech/smartspectra_demo/MainActivity.kt) for example usage and plotting of a pulse and breathing pleth waveform. 
### Implementing the Callback Interface
Implement `SmartSpectraResultListener` in your MainActivity:
```kotlin
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
```
### Data Format
The resultsListener contains the following objects:

-  `result.strictPulseRate` - (Double) the strict pulse rate (high confidence average over 30 seconds)
-  `result.strictBreathingRate` - (Double) the strict breathing rate (high confidence average over 30 seconds)
-  `result.pulseValues` - ArrayList<>(time (double), value (double)) Pulse rates 
- `result.pulseConfidence` - ArrayList<>(time (double), value (double)) Pulse rate confidences
- `result.pulsePleth` - ArrayList<>(time (double), value (double)) Pulse waveform or pleth 
- `result.breathingValues` - ArrayList<>(time (double), value (double)) Breathing rates
- `result.breathingPleth` - ArrayList<>(time (double), value (double)) Breathing movement waveform or pleth
- `result.breathingAmplitude` - ArrayList<>(time (double), value (double)) Breathing rate confidences
- `result.apnea` - ArrayList<>(time (double), value (bool)) Apnea detection
- `result.breathingBaseline` - ArrayList<>(time (double), value (double)) Breathing baseline
- `result.phasic` - ArrayList<>(time (double), value (double)) Phasic (ie changes in relative blood pressure)
- `result.rrl` - ArrayList<>(time (double), value (double)) Respiratory line length 
- `result.ie` -  ArrayList<>(time (double), value (double)) The inhale exhale ratio 
- `result.upload_date` - ZonedDateTime - upload date time
- `result.version` - String - the version of API used
- 



## API Key
You can obtain an API key from PresageTech's developer portal (https://physiology.presagetech.com/)

## Troubleshooting
For additional support, contact support@presagetech.com or submit a github issue (https://github.com/Presage-Security/SmartSpectra-Android-App/issues)
## Known Bugs: 
- Will sometimes crash on initial launch after accepting camera permissions
- HRV is not returning 

### To dos:
- add in user acceptance prompt and tutorial

