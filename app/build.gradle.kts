plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        applicationId = "de.schnettler.tvtracker"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["dagger.hilt.disableModulesHaveInstallInCheck"] = "true"
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures{
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    kapt {
        correctErrorTypes = true
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
        }
    }
}

dependencies {
    implementation(Kotlin.stdlib.jdk8)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.coreKtx)
    implementation(AndroidX.navigation.fragmentKtx)
    implementation(AndroidX.navigation.uiKtx)
    implementation(AndroidX.lifecycle.extensions)
    implementation(AndroidX.lifecycle.liveDataKtx)
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.lifecycle.viewModelSavedState)
    implementation(AndroidX.room.runtime)
    implementation(AndroidX.room.ktx)
    implementation(AndroidX.swipeRefreshLayout)
    implementation(AndroidX.paging.runtimeKtx)
    implementation(AndroidX.browser)
    implementation(AndroidX.transition)
    implementation(Google.android.material)
    implementation(AndroidX.preferenceKtx)
    implementation(Square.retrofit2.retrofit)
    implementation(Square.retrofit2.converter.moshi)
    implementation(Square.retrofit2.converter.scalars)
    implementation(Square.okHttp3.okHttp)
    implementation(JakeWharton.timber)
    kapt(AndroidX.room.compiler)

    // Other
    implementation("dev.chrisbanes", "insetter-dbx", "_")
    implementation("dev.chrisbanes", "insetter-ktx", "_")
    implementation("com.squareup.moshi", "moshi-kotlin", "_")
    implementation("io.coil-kt", "coil", "_")
    implementation("com.airbnb.android", "epoxy", "_")
    implementation("com.airbnb.android", "epoxy-databinding", "_")
    implementation("com.airbnb.android", "epoxy-paging", "_")
    implementation("com.github.etiennelenhart", "eiffel", "_")
    implementation("com.jakewharton.threetenabp", "threetenabp", "_")
    implementation("com.facebook.stetho", "stetho", "_")
    implementation("com.facebook.stetho", "stetho-okhttp3", "_")
    implementation("com.ryanjeffreybrooks", "indefinitepagerindicator", "_")
    implementation("com.dropbox.mobile.store", "store4", "_")
    debugImplementation("com.amitshekhar.android", "debug-db", "_")
    implementation("com.google.dagger", "hilt-android", "_")
    implementation("androidx.hilt", "hilt-lifecycle-viewmodel", "_")
    compileOnly ("com.squareup.inject", "assisted-inject-annotations-dagger2", "_")
    kapt("com.squareup.inject", "assisted-inject-processor-dagger2", "_")
    kapt("androidx.hilt", "hilt-compiler", "_")
    kapt("com.google.dagger", "hilt-android-compiler", "_")
    kapt("com.airbnb.android", "epoxy-processor", "_")
}