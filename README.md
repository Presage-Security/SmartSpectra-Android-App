# SmartSpectra SDK Integration Guide

This README provides instructions for integrating and utilizing the SmartSpectra SDK in your Android application to measure physiology metrics from video.

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
