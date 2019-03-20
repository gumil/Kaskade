import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

apply { from(rootProject.file("gradle/kotlin-sources.gradle")) }

android {
    compileSdkVersion(deps.android.build.compileSdkVersion)
    buildToolsVersion(deps.android.build.buildToolsVersion)

    defaultConfig {
        applicationId = "dev.gumil.kaskade.sample"
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

androidExtensions {
    configure(delegateClosureOf<AndroidExtensionsExtension> {
        isExperimental = true
    })
}

dependencies {
    implementation(deps.android.appCompat)
    implementation(deps.android.recyclerView)
    implementation(deps.android.material)

    implementation(deps.kotlin.stdlib.jdk8)
    implementation(deps.kotlin.coroutines.core)
    implementation(deps.kotlin.coroutines.android)

    implementation(project(":core"))
    implementation(project(":livedata"))
    implementation(project(":coroutines"))
    implementation(project(":rx"))

    implementation(deps.retrofit.core)
    implementation(deps.retrofit.moshi)
    implementation(deps.retrofit.logging)
    implementation(deps.retrofit.coroutines)

    implementation(deps.libs.picasso)

    implementation(deps.android.lifecycle.extensions)
    implementation(deps.android.navigation)

    implementation(deps.rx.java)
    implementation(deps.rx.android)
    implementation(deps.rx.binding)

    testImplementation(deps.android.lifecycle.test)
    testImplementation(deps.kotlin.test.junit)
    testImplementation(deps.test.mockK)
}
