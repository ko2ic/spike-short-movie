plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.devtools.ksp").version("1.9.10-1.0.13")
    id("com.google.dagger.hilt.android")

}

android {
    namespace = "com.ko2ic.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ko2ic.sample"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-compiler:2.48.1")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("androidx.activity:activity-ktx:1.8.1")
    implementation("io.coil-kt:coil:1.2.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")

    // Only needed for RedditVideoDataRepository
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi:1.14.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")

    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("io.github.java-diff-utils:java-diff-utils:4.11")

    implementation("androidx.viewpager2:viewpager2:1.1.0-beta02")

    debugImplementation("com.willowtreeapps.hyperion:hyperion-core:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-attr:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-build-config:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-crash:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-disk:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-geiger-counter:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-measurement:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-recorder:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-shared-preferences:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-timber:0.9.34")
}

kapt {
    correctErrorTypes = true
}