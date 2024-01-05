# SmartSpectra SDK. Heart and Respiration rate measurment demo.

## Sign up for an API key
https://physiology.presagetech.com/

## Add it to your application

### Sdk dependency

Add sdk dependency to your build.gradle.kts file
```kotlin
implementation("com.presagetech:smartspectra:1.0.0-SNAPSHOT")
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

### Code changes

Then add `com.presagetech.smartspectra.core.button.SmartSpectraButton` into your activity.

SmartSpectraButton has two methods:
* `.setApiKey(String)` - which expects you to provide your api key before using the button,
* `.setResultListener(SmartSpectraResultListener)` - to handle measurment results.

Please refer to [MainActivity.kt](app/src/main/java/com/presagetech/smartspectra_demo/MainActivity.kt) for usage example.
