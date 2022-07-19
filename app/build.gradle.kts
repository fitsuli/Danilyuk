plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
}

val composeVersion = "1.3.0-alpha01"
val accompanistVersion = "0.24.13-rc"

android {
    compileSdk = 32
    signingConfigs {
    }

    buildFeatures.compose = true

    defaultConfig {
        applicationId = "ru.fitsuli.developerslifeviewer"
        minSdk = 23
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packagingOptions {
        resources.excludes.add("org/**")
        resources.excludes.add("DebugProbesKt.bin")
        resources.excludes.add("build-data.properties")
        resources.excludes.add("kotlin-tooling-metadata.json")
    }

    composeOptions.kotlinCompilerExtensionVersion = "1.2.0"
    kotlinOptions.jvmTarget = "11"
}

dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.preference:preference-ktx:1.2.0") {
        exclude(group = "androidx.appcompat")
        exclude(group = "androidx.fragment")
        exclude(group = "androidx.preference", module = "preference")
    }
    implementation("androidx.preference:preference:1.2.0") {
        exclude(group = "androidx.appcompat")
        exclude(group = "androidx.fragment")
        exclude(group = "androidx.recyclerview")
        exclude(group = "androidx.slidingpanelayout")
    }

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0")
    implementation("androidx.lifecycle:lifecycle-common:2.5.0")
    implementation("androidx.core:core-splashscreen:1.0.0-rc01")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Compose
    implementation("androidx.activity:activity-compose:1.6.0-alpha05")
    implementation("androidx.compose.material3:material3:1.0.0-alpha14")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.animation:animation:$composeVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")

    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanistVersion")
    implementation("io.coil-kt:coil-compose:1.4.0")
    implementation("io.coil-kt:coil-gif:1.4.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // Tests (unused)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation ("androidx.compose.ui:ui-test-manifest:$composeVersion")
}

