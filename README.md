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
    implementation("com.presagetech:smartspectra:1.0.15-SNAPSHOT")
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
In your activity or fragment, initialize the `SmartSpectraView` (The view consists of checkup button and result view):

```kotlin
import com.presagetech.smartspectra.SmartSpectraView

 override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setting up SmartSpectra Results/Views
        smartSpectraView = findViewById(R.id.smart_spectra_view)

 }
```
### Set API Key and Configure SDK Paramters

You need a valid API key to authenticate your requests:

```kotlin
//Required configuration
// Your api token from https://physiology.presagetech.com/
smartSpectraView.setApiKey("YOUR_API_KEY")

// Optional configurations
// Set measurement duration (valid range for spot time is between 20.0 and 120.0)
// Defaults to 30 if not specified otherwise
smartSpectraView.setSpotTime(30.0)
//whether to show fps in the previewDisplay
smartSpectraView.setShowFps(false)
```
You can obtain an API key from PresageTech's developer portal (https://physiology.presagetech.com/)

## Usage

### Example Code

To retrieve and use metrics, you can attach a `metricsBufferObserver` to get the metrics to process. Please refer to [MainActivity.kt](src/main/java/com/presagetech/smartspectra_example/MainActivity.kt) for example usage and plotting of different metrics such as pulse rate, breathing rates etc.

```kotlin
import com.presage.physiology.proto.MetricsProto.MetricsBuffer

override fun onCreate(savedInstanceState: Bundle?) {
    //...
    //...
    smartSpectraView.setMetricsBufferObserver { metricsBuffer ->
        // Process meshPoints here
        handleMetricsBuffer(metricsBuffer)
    }
    //...
    //...
}

private fun handleMetricsBuffer(metrics: MetricsBuffer) {
    // get the relevant metrics
    val pulse = metrics.pulse
    val breathing = metrics.breathing

    // Plot the results

    // Pulse plots
    if (pulse.traceCount > 0) {
        addChart(pulse.traceList.map { Entry(it.time, it.value) },  "Pulse Pleth", false)
    }
    // Breathing plots
    if (breathing.upperTraceCount > 0) {
        addChart(breathing.upperTraceList.map { Entry(it.time, it.value) }, "Breathing Pleth", false)
    }
    // TODO: See examples of plotting other metrics in MainActivity.kt
}

```

For facemesh points, you can attach a `meshPointsObserver` to get the mesh points to process. To see an complete example of using scatter chart to visualize the mesh points, please refer to [MainActivity.kt](src/main/java/com/presagetech/smartspectra_example/MainActivity.kt). Reference to the index of the mesh points and their corresponding face landmarks can be seen [here](https://storage.googleapis.com/mediapipe-assets/documentation/mediapipe_face_landmark_fullsize.png)

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    //...
    //...
    smartSpectraView.setMeshPointsObserver { meshPoints ->
        // Process meshPoints here
        handleMeshPoints(meshPoints)
    }
    //...
    //...
}
private fun handleMeshPoints(meshPoints: List<Pair<Int, Int>>) {
    Timber.d("Observed mesh points: ${meshPoints.size}")
// TODO: Update UI or handle the points as needed. See examples of plotting in MainActivity.kt
}


```

### Detailed `MetricsBuffer` Class Descriptions

> **TIP**
> If you need to use the types directly, the `MetricsBuffer` and corresponding classes are under the `com.presage.physiology.proto.MetricsProto` namespace. You can import it from `MetricsProto.MetricsBuffer` for easier usage:
> ```kotlin
> import com.presage.physiology.proto.MetricsProto.MetricsBuffer
> ```

`MetricsBuffer` contains the following parent classes:

```kotlin
class MetricsBuffer {
    var pulse: Pulse
    var breathing: Breathing
    var bloodPressure: BloodPressure
    var face: Face
    var metadata: Metadata
}
```

### Measurement Types

- **`Measurement` Class**: Represents a measurement with time and value:

```kotlin
class Measurement {
    var time: Float
    var value: Float
    var stable: Boolean
}
```

- **`MeasurementWithConfidence` Class**: Includes confidence with the measurement:

```kotlin
class MeasurementWithConfidence {
    var time: Float
    var value: Float
    var stable: Boolean
    var confidence: Float
}
```

- **`DetectionStatus` Class**: Used for events like apnea or face detection (blinking/talking):

```kotlin
class DetectionStatus {
    var time: Float
    var detected: Boolean
    var stable: Boolean
}
```

#### Metric Types

- **`Pulse` Class**: Contains pulse-related measurements, including rate, trace, and strict values:

```kotlin
class Pulse {
    var rateList: List<MeasurementWithConfidence>
    var traceList: List<Measurement>
    var strict: Strict
}
```

- **`Breathing` Class**: Handles breathing-related data with upper and lower traces, amplitude, apnea status, and other metrics:

```kotlin
class Breathing {
    var rateList: List<MeasurementWithConfidence>
    var upperTraceList: List<Measurement>
    var lowerTraceList: List<Measurement>
    var amplitudeList: List<Measurement>
    var apneaList: List<DetectionStatus>
    var respiratoryLineLengthList: List<Measurement>
    var inhaleExhaleRatioList: List<Measurement>
    var strict: Strict
}
```

- **`BloodPressure` Class**: Handles blood pressure measurements:

> [!CAUTION]
> Currently not available publicly, currently returned results are a duplicate of pulse pleth

```kotlin
class BloodPressure {
    var phasicList: List<MeasurementWithConfidence>
}
```

- **`Face` Class**: Includes detection statuses for blinking and talking:

```kotlin
class Face {
    var blinkingList: List<DetectionStatus>
    var talkingList: List<DetectionStatus>
}
```

- **`Metadata` Class**: Includes metadata information:

```kotlin
class Metadata {
    var id: String
    var uploadTimestamp: String
    var apiVersion: String
}
```

#### Encoding and Decoding Protobuf Messages

To serialize `MetricsBuffer` into binary format:

```kotlin
try {
    val data: ByteArray = metricsBuffer.toByteArray()
    // Send `data` to your backend or save it
} catch (e: Exception) {
    Timber.e("Failed to serialize metrics: ${e.message}")
}
```

To decode binary protobuf data into `MetricsBuffer`:

```kotlin
try {
    val decodedMetrics = MetricsBuffer.parseFrom(data)
    // Use `decodedMetrics` as needed
} catch (e: Exception) {
    Timber.e("Failed to decode metrics: ${e.message}")
}
```

## API Key

You can obtain an API key from PresageTech's developer portal (https://physiology.presagetech.com/)

## Troubleshooting

For additional support, contact support@presagetech.com or [submit a github issue](https://github.com/Presage-Security/SmartSpectra-Android-App/issues)

### Known Bugs

- Currently, there are no known bugs. If you encounter an issue, please contact support or report it.
