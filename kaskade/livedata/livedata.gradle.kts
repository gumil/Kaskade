import plugin.AndroidConfigurationPlugin

plugins {
    id("com.android.library")
    kotlin("android")
}

apply<AndroidConfigurationPlugin>()

android {
    buildTypes {
        getByName("debug") {
            isTestCoverageEnabled = true
        }
    }

    lintOptions {
        isAbortOnError = true
    }
}

dependencies {
    implementation(project(":core"))

    implementation(deps.kotlin.stdlib.core)
    implementation(deps.android.lifecycle.livedata)

    testImplementation(deps.android.lifecycle.test)
    testImplementation(deps.kotlin.test.junit)
    testImplementation(deps.test.mockK)
}

apply {
    from(rootProject.file("gradle/maven.gradle"))
    from(rootProject.file("gradle/bintray.gradle"))
}
