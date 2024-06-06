plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.presagetech.smartspectra_example"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.presagetech.smartspectra_example"
        minSdk = 24
        targetSdk = 34
	versionCode = 4
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0") // for plotting
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.presagetech:smartspectra:1.0.3-SNAPSHOT")
}
