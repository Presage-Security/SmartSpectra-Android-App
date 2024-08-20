# Maven SmartSpectra SDK Integration Guide

This provides instructions for integrating and utilizing the Presage SmartSpectra SDK publicly hosted on Maven in your Android application to measure physiology metrics from a 30 second measurement using the mobile device's camera.

This app contained in this repo is an example of pulling and using the SmartSpectra SDK from Maven. It should run out of the box as long as your API key is provided.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Setup](#setup)
- [Usage](#usage)
- [API Key](#api-key)
- [Troubleshooting](#troubleshooting)
- [Known Bugs](#known-bugs)


## Prerequisites
Before you start, ensure your development environment includes:
- Android Studio Giraffe or later
- Minimum SDK level 26

## Installation
To integrate the SmartSpectra SDK into your Android project, add the following dependency to your app's `build.gradle` file:

```gradle
dependencies {
    implementation 'com.presagetech:smartspectra:1.0.10'
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
In your activity or fragment, initialize the `SmartSpectraButton`, `SmartSpectraResultListener`, `SmartSpectraResultView`, 
and `ScreeningResult`:

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
 }
```
### Set API Key and Configure SDK Paramters

You need a valid API key to authenticate your requests:

```kotlin
smartSpectraButton.setApiKey("YOUR_API_KEY")

// Set measurement duration (valid range for spot time is between 20.0 and 120.0)
// Defaults to 30 if not specified otherwise
smartSpectraButton.setSpotTime(30.0)
//whether to show fps in the previewDisplay
smartSpectraButton.setShowFps(false)
```
You can obtain an API key from PresageTech's developer portal (https://physiology.presagetech.com/)

## Usage

### Example Code

Implement `SmartSpectraResultListener` in your MainActivity. Please refer to [MainActivity.kt](app/src/main/java/com/presagetech/smartspectra_example/MainActivity.kt) for example usage and plotting of a pulse and breathing pleth waveform. 

```kotlin
private val resultListener: SmartSpectraResultListener = SmartSpectraResultListener { result ->

        resultView.onResult(result) // pass the result to the view or handle it as needed

        // example usage of pulse and breathing pleth data (if present) to plot the pleth charts
        // see [MainActivity.kt](app/src/main/java/com/presagetech/smartspectra_example/MainActivity.kt)
        if (result is ScreeningResult.Success) {
            result.pulsePleth?.let {
                addChart( it.map { Entry(it.time, it.value) }, "Pulse Pleth", false)
            }
            result.breathingPleth?.let {
                addChart( it.map { Entry(it.time, it.value) }, "Breathing Pleth", false)
            }
        }
    }
```

### Data Format

The resultsListener contains the following objects:


| Result Key                   | Value Type                                 | Description                                                            |
|------------------------------|--------------------------------------------|------------------------------------------------------------------------|
| `result.strictPulseRate`     | (Double)                                   | The strict pulse rate (high confidence average over spot duration)     |
| `result.strictBreathingRate` | (Double)                                   | The strict breathing rate (high confidence average over spot duration) |
| `result.pulseValues`         | ArrayList<>(time (double), value (double)) | Pulse rates                                                            |
| `result.pulseConfidence`     | ArrayList<>(time (double), value (double)) | Pulse rate confidences                                                 |
| `result.pulsePleth`          | ArrayList<>(time (double), value (double)) | Pulse waveform or pleth                                                |
| `result.hrv`                 | ArrayList<>(time (double), value (double)) | Pulse rate variability (RMSSD) **(Requires 60+ second Spot Duration)** |
| `result.breathingValues`     | ArrayList<>(time (double), value (double)) | Breathing rates                                                        |
| `result.breathingPleth`      | ArrayList<>(time (double), value (double)) | Breathing movement waveform or pleth                                   |
| `result.breathingAmplitude`  | ArrayList<>(time (double), value (double)) | Breathing rate confidences                                             |
| `result.apnea`               | ArrayList<>(time (double), value (bool))   | Apnea detection                                                        |
| `result.breathingBaseline`   | ArrayList<>(time (double), value (double)) | Breathing baseline                                                     |
| `result.phasic`              | ArrayList<>(time (double), value (double)) | Phasic (ie changes in relative blood pressure)                         |
| `result.rrl`                 | ArrayList<>(time (double), value (double)) | Respiratory line length                                                |
| `result.ie`                  | ArrayList<>(time (double), value (double)) | The inhale exhale ratio                                                |
| `result.upload_date`         | ZonedDateTime                              | upload date time                                                       |
| `result.version`             | String                                     | the version of API used                                                |
|                              |                                            |                                                                        |

## API Key

You can obtain an API key from PresageTech's developer portal (https://physiology.presagetech.com/)

## Troubleshooting
 
For additional support, contact support@presagetech.com or submit a github issue (https://github.com/Presage-Security/SmartSpectra-Android-App/issues)

[//]: # (## Known Bugs)


