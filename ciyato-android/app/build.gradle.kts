plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ciyato.launcher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ciyato.launcher"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0-beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // BuildConfig flags (Suggestion #113)
        // Access in code as BuildConfig.WEATHER_BASE_URL, BuildConfig.IS_INTERNAL, etc.
        buildConfigField("String",  "WEATHER_BASE_URL",    "\"https://api.open-meteo.com/v1\"")
        buildConfigField("String",  "AQI_BASE_URL",        "\"https://air-quality-api.open-meteo.com/v1\"")
        buildConfigField("String",  "GEOCODE_BASE_URL",    "\"https://nominatim.openstreetmap.org\"")
        buildConfigField("long",    "WEATHER_CACHE_TTL_MS","1800000L") // 30 minutes
        buildConfigField("int",     "MAX_CRASH_LOGS",      "10")
        buildConfigField("boolean", "IS_INTERNAL",         "false")
    }

    buildTypes {
        release {
            isMinifyEnabled = true    // Enable R8/ProGuard in release (Suggestion 114)
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            // Override BuildConfig flags for release
            buildConfigField("boolean", "IS_INTERNAL", "false")
        }
        debug {
            isDebuggable = true
            // Enable debug stubs for weather/location in internal builds
            buildConfigField("boolean", "IS_INTERNAL", "true")
        }
    }

    // ── Output APK naming ─────────────────────────────────────────────────────
    applicationVariants.all {
        val variant = this
        variant.outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output.outputFileName = "Ciyato.apk"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        // Enable Compose compiler stability checks (Suggestion 115)
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.runtime.ExperimentalComposeApi",
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true   // Enable BuildConfig generation (Suggestion 113)
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    // DocumentFile — used by FileCollectionDetailScreen to read SAF folder contents
    implementation("androidx.documentfile:documentfile:1.0.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// ── After build: copy APK to project root for easy access ─────────────────────
tasks.whenTaskAdded {
    if (name == "assembleDebug") {
        doLast {
            val buildOutputDir = layout.buildDirectory.get().asFile
            val apkSrc = file("${buildOutputDir}/outputs/apk/debug/Ciyato.apk")
            val apkDst = file("${rootDir}/Ciyato.apk")
            if (apkSrc.exists()) {
                apkSrc.copyTo(apkDst, overwrite = true)
                println("✅ APK ready at: ${apkDst.absolutePath}")
            }
        }
    }
}
