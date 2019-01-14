plugins {
    id("com.android.application")
    id("androidx.navigation.safeargs")
    id("kotlin-android-extensions")
    kotlin("android")
}

apply { from(rootProject.file("gradle/kotlin-sources.gradle")) }

android {
    compileSdkVersion(deps.android.build.compileSdkVersion)
    buildToolsVersion(deps.android.build.buildToolsVersion)

    defaultConfig {
        applicationId = "io.gumil.kaskade.sample"
        minSdkVersion(deps.android.build.sampleMinSdkVersion)
        targetSdkVersion(deps.android.build.targetSdkVersion)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), file("proguard-rules.pro"))
        }
    }
}

dependencies {
    implementation(deps.android.appCompat)
    implementation(deps.android.recyclerView)
    implementation(deps.android.material)

    implementation(deps.kotlin.stdlib.jdk8)
    implementation(deps.kotlin.coroutines.core)
    implementation(deps.kotlin.coroutines.android)

    implementation(project(":kaskade"))
    implementation(project(":kaskade-livedata"))
    implementation(project(":kaskade-coroutines"))
    implementation(project(":kaskade-rx"))

    implementation(deps.retrofit.core)
    implementation(deps.retrofit.moshi)
    implementation(deps.retrofit.logging)
    implementation(deps.retrofit.coroutines)

    implementation(deps.picasso)

    implementation(deps.android.lifecycle.extensions)
    implementation(deps.android.navigation)

    implementation(deps.rx.java)
    implementation(deps.rx.android)
    implementation(deps.rx.binding)

    testImplementation(deps.test.lifecycle)
    testImplementation(deps.test.kotlinjUnit)
    testImplementation(deps.test.mockK)
}
