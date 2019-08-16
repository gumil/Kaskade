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

    packagingOptions {
        pickFirst("META-INF/atomicfu.kotlin_module")
        pickFirst("META-INF/kotlinx-coroutines-core.kotlin_module")
    }
}

androidExtensions {
    isExperimental = true
}

dependencies {
    implementation(deps.android.appCompat)
    implementation(deps.android.recyclerView)
    implementation(deps.android.material)

    implementation(deps.kotlin.stdlib.jdk8)
    implementation(deps.kotlin.coroutines.core)
    implementation(deps.kotlin.coroutines.android)

    implementation(deps.kaskade.core)
    implementation(deps.kaskade.coroutines)
    implementation(deps.kaskade.livedata)
    implementation(deps.kaskade.rx)

    implementation(deps.retrofit.core)
    implementation(deps.retrofit.moshi)
    implementation(deps.retrofit.logging)

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