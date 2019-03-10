@file:Suppress("unused", "ClassName")

object versions {
    const val jacoco = "0.8.3"
    const val detekt = "1.0.0-RC12"

    internal const val kotlin = "1.3.20"
    internal const val navigation = "2.0.0-rc02"
    internal const val coroutines = "1.1.0"
    internal const val lifecycle = "2.0.0"
    internal const val retrofit = "2.5.0"
}

object deps {
    object android {
        object build {
            const val buildToolsVersion = "28.0.3"
            const val compileSdkVersion = 28
            const val minSdkVersion = 16
            const val sampleMinSdkVersion = 21
            const val targetSdkVersion = 28

            const val gradlePlugin = "com.android.tools.build:gradle:3.3.0"
        }

        object lifecycle {
            const val livedata = "androidx.lifecycle:lifecycle-livedata:${versions.lifecycle}"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:${versions.lifecycle}"
        }

        const val appCompat = "androidx.appcompat:appcompat:1.0.2"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.0.0"
        const val material = "com.google.android.material:material:1.1.0-alpha02"
        const val navigation = "androidx.navigation:navigation-fragment:${versions.navigation}"
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
            const val detekt = "io.gitlab.arturbosch.detekt"
        }
    }

    object detekt {
        const val lint = "io.gitlab.arturbosch.detekt:detekt-formatting:${versions.detekt}"
    }

    object test {
        const val kotlinjUnit = "org.jetbrains.kotlin:kotlin-test-junit:${versions.kotlin}"
        const val mockK = "io.mockk:mockk:1.9.1"
        const val lifecycle = "androidx.arch.core:core-testing:${versions.lifecycle}"
    }
}