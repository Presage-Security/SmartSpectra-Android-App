# SmartSpectra SDK Integration Guide

This provides instructions for integrating and utilizing the SmartSpectra SDK in your Android application to measure physiology metrics from a 30 second measurement using the mobile device's camera.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Setup](#setup)
- [Usage](#usage)
- [API Key](#api-key)
- [Handling Results](#handling-results)
- [Troubleshooting](#troubleshooting)


## Prerequisites
Before you start, ensure your development environment includes:
- Android Studio 4.0 or higher
- Minimum SDK level 21

## Installation
To integrate the SmartSpectra SDK into your Android project, add the following dependency to your app's `build.gradle` file:

```gradle
dependencies {
    implementation 'com.presagetech:smartspectra:1.0.2'
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
In your activity or fragment, initialize the SmartSpectraButton and SmartSpectraResultView:
```kotlin
val smartSpectraButton: SmartSpectraButton = findViewById(R.id.btn)
val resultView: SmartSpectraResultView = findViewById(R.id.result_view)
```
### Set API Key
You need a valid API key to authenticate your requests:
```kotlin
smartSpectraButton.setApiKey("YOUR_API_KEY")
```

## Usage
### Example Code
Please refer to [MainActivity.kt](app/src/main/java/com/presagetech/smartspectra_demo/MainActivity.kt) for example usage and plotting of a pulse pleth waveform. 
### Implementing the Callback Interface
Implement `SmartSpectraResultView.SmartSpectraResultsCallback` in your MainActivity:
```kotlin
class MainActivity : AppCompatActivity(), SmartSpectraResultView.SmartSpectraResultsCallback {
    override fun onMetricsJsonReceive(jsonMetrics: JSONObject) {
        // Handle JSON metrics here
    }

    override fun onStrictPuleRateReceived(strictPulseRate: Int) {
        // Handle strict pulse rate
    }

    override fun onStrictBreathingRateReceived(strictBreathingRate: Int) {
        // Handle strict breathing rate
    }
}
```
### Data Format
- `jsonMetrics` is a json containing the metrics available according to your api key. For com.presagetech:smartspectra:1.0.2 the current json structure is as follows:
 ```json
{
  "error": "",
  "version": "3.10.1",
  "pulse": {
     "hr":{
         "10":{
              "value": 58.9,
              "confidence": 0.95,
         },
         "11":{
              "value": 58.2,
              "confidence": 0.94,
         },
         "12":{
              "value": 58.1,
              "confidence": 0.91,
         },
      },
     "hr_trace":{
         "0":{ "value": 0.5},
         "0.033":{ "value": 0.56},
         "0.066":{ "value": 0.59}
      },
     "hr_spec":{
         "10":{ "value": [], "freq":[]},
         "11":{ "value": [], "freq":[]},
         "12":{ "value": [], "freq":[]}
      },
     "hrv":{},
  },
  "breath": {
     "rr":{
         "15":{
              "value": 18.9,
              "confidence": 0.95,
         },
         "16":{
              "value": 18.2,
              "confidence": 0.94,
         },
         "17":{
              "value": 18.1,
              "confidence": 0.91,
         },
      },
     "rr_trace":{
         "0":{ "value": 0.5},
         "0.033":{ "value": 0.56},
         "0.066":{ "value": 0.59}
      },
     "rr_spec":{
         "15":{ "value": [], "freq":[]},
         "16":{ "value": [], "freq":[]},
         "17":{ "value": [], "freq":[]}
      },
     "rrl":{"0":{ "value": 0.5}},
     "apnea":{"0":{ "value": false}},
     "ie":{"0":{ "value": 1.5}},
     "amplitude":{"0":{ "value": 0.5}},
     "baseline":{"0":{ "value": 0.5}}
  },
  "pressure": {
     "phasic":{"0":{ "value": 0.5}},
  }
}
```
- `strictPulseRate` is a single integer value representing the Strict Pulse Rate in beats per min which is the average of only high confidence pulse rate values
- `strictBreathingRate` is a single integer value representing the Strict Breathing Rate in beats per min which is the average of only high confidence pulse rate values

### Setting Up Listeners
Connect your components to the callbacks:
```kotlin
resultView.callback = this
smartSpectraButton.setResultListener(resultView)
```

## API Key
You can obtain an API key from PresageTech's developer portal (https://physiology.presagetech.com/)

## Troubleshooting
For additional support, contact support@presagetech.com or submit a github issue (https://github.com/Presage-Security/SmartSpectra-Android-App/issues)

Please refer to [MainActivity.kt](app/src/main/java/com/presagetech/smartspectra_demo/MainActivity.kt) for usage example.
