plugins {
    id("com.android.library")
    id("com.github.dcendents.android-maven")
    kotlin("android")
}

apply { from(rootProject.file("gradle/kotlin-sources.gradle")) }

android {
    compileSdkVersion(deps.android.build.compileSdkVersion)

    defaultConfig {
        minSdkVersion(deps.android.build.minSdkVersion)
        targetSdkVersion(deps.android.build.targetSdkVersion)
    }
}

dependencies {
    implementation(project(":kaskade"))

    implementation(deps.kotlin.stdlib.core)
    implementation(deps.android.lifecycle.livedata)

    testImplementation(deps.test.lifecycle)
    testImplementation(deps.test.kotlinjUnit)
}
