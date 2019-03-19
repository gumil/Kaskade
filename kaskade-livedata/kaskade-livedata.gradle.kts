plugins {
    id("com.android.library")
    kotlin("android")
    id(deps.bintray.plugin)
}

apply { from(rootProject.file("gradle/kotlin-sources.gradle")) }

android {
    compileSdkVersion(deps.android.build.compileSdkVersion)

    defaultConfig {
        minSdkVersion(deps.android.build.minSdkVersion)
        targetSdkVersion(deps.android.build.targetSdkVersion)
    }

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
    implementation(project(":kaskade"))

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
