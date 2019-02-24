@file:Suppress("unused", "ClassName")

/**
 * Public versions
 */
object versions {
    const val jacoco = "0.8.3"
}

object deps {
    private object versions {
        const val kotlin = "1.3.20"
        const val navigation = "1.0.0-alpha09"
        const val ktlint = "6.3.1"
        const val coroutines = "1.1.0"
        const val lifecycle = "2.0.0"
        const val retrofit = "2.5.0"
    }

    object android {
        object build {
            const val buildToolsVersion = "28.0.3"
            const val compileSdkVersion = 28
            const val minSdkVersion = 16
            const val sampleMinSdkVersion = 21
            const val targetSdkVersion = 28

            const val gradlePlugin = "com.android.tools.build:gradle:3.3.0"
            const val navigationPlugin = "android.arch.navigation:navigation-safe-args-gradle-plugin:${versions.navigation}"
        }

        object lifecycle {
            const val livedata = "androidx.lifecycle:lifecycle-livedata:${versions.lifecycle}"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:${versions.lifecycle}"
        }

        const val appCompat = "androidx.appcompat:appcompat:1.0.2"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.0.0"
        const val material = "com.google.android.material:material:1.1.0-alpha02"
        const val navigation = "android.arch.navigation:navigation-fragment:1.0.0-alpha09"
    }

    object kotlin {
        object build {
            const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlin}"
        }

        object stdlib {
            const val core = "org.jetbrains.kotlin:kotlin-stdlib:${versions.kotlin}"
            const val jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${versions.kotlin}"
        }

        object coroutines {
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.coroutines}"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${versions.coroutines}"
        }
    }

    object rx {
        const val java = "io.reactivex.rxjava2:rxjava:2.2.4"
        const val android = "io.reactivex.rxjava2:rxandroid:2.1.0"
        const val binding = "com.jakewharton.rxbinding3:rxbinding:3.0.0-alpha1"
    }

    object retrofit {
        const val core = "com.squareup.retrofit2:retrofit:${versions.retrofit}"
        const val moshi = "com.squareup.retrofit2:converter-moshi:${versions.retrofit}"
        const val logging = "com.squareup.okhttp3:logging-interceptor:3.12.0"
        const val coroutines = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"
    }

    const val picasso = "com.squareup.picasso:picasso:2.71828"

    object build {
        object repositories {
            const val plugins = "https://plugins.gradle.org/m2/"
        }

        object plugins {
            const val androidMaven = "com.github.dcendents:android-maven-gradle-plugin:2.1"
            const val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:${versions.ktlint}"
        }
    }

    object test {
        const val kotlinjUnit = "org.jetbrains.kotlin:kotlin-test-junit:${versions.kotlin}"
        const val mockK = "io.mockk:mockk:1.8.13.kotlin13"
        const val lifecycle = "androidx.arch.core:core-testing:${versions.lifecycle}"
    }
}