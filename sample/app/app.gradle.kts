import plugin.AndroidConfigurationPlugin

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

apply<AndroidConfigurationPlugin>()

android {
    defaultConfig {
        minSdkVersion(deps.android.build.sampleMinSdkVersion)
        applicationId = "dev.gumil.kaskade.sample"
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

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

androidExtensions {
    isExperimental = true
}

dependencies {
    implementation(deps.sample.android.appCompat)
    implementation(deps.sample.android.recyclerView)
    implementation(deps.sample.android.material)

    implementation(deps.kotlin.stdlib.jdk8)
    implementation(deps.kotlin.coroutines.core)
    implementation(deps.kotlin.coroutines.android)

    implementation(project(":core"))
    implementation(project(":coroutines"))
    implementation(project(":livedata"))
    implementation(project(":rx"))

    implementation(deps.sample.retrofit.core)
    implementation(deps.sample.retrofit.moshi)
    implementation(deps.sample.retrofit.logging)

    implementation(deps.sample.picasso)

    implementation(deps.sample.lifecycle.extensions)
    implementation(deps.sample.android.navigation)

    implementation(deps.rx.java)
    implementation(deps.sample.rx.android)
    implementation(deps.sample.rx.binding)

    testImplementation(deps.android.lifecycle.test)
    testImplementation(deps.kotlin.test.junit)
    testImplementation(deps.test.mockK)
}
