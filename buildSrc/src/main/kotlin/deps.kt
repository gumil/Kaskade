@file:Suppress("unused", "ClassName")

object versions {
    const val jacoco = "0.8.4"
    const val detekt = "1.0.1"
    const val node = "1.3.1"

    internal const val kotlin = "1.3.50"
    internal const val navigation = "2.1.0-rc01"
    internal const val coroutines = "1.3.2"
    internal const val lifecycle = "2.0.0"
    internal const val retrofit = "2.6.1"
    internal const val bintray = "1.8.4"
}

object deps {
    object android {
        object build {
            const val buildToolsVersion = "29.0.2"
            const val compileSdkVersion = 29
            const val minSdkVersion = 16
            const val sampleMinSdkVersion = 21
            const val targetSdkVersion = 29
        }

        const val classpath = "com.android.tools.build:gradle:3.5.1"

        object lifecycle {
            const val livedata = "androidx.lifecycle:lifecycle-livedata:${versions.lifecycle}"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:${versions.lifecycle}"
            const val test = "androidx.arch.core:core-testing:${versions.lifecycle}"
        }

        const val appCompat = "androidx.appcompat:appcompat:1.1.0-rc01"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.1.0-beta03"
        const val material = "com.google.android.material:material:1.2.0-alpha01"
        const val navigation = "androidx.navigation:navigation-fragment:${versions.navigation}"
    }

    object kotlin {
        const val classpath = "org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlin}"

        object stdlib {
            const val core = "org.jetbrains.kotlin:kotlin-stdlib:${versions.kotlin}"
            const val jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${versions.kotlin}"
        }

        object coroutines {
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.coroutines}"
            const val native = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${versions.coroutines}"
            const val js = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${versions.coroutines}"
            const val common = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${versions.coroutines}"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${versions.coroutines}"
        }

        object test {
            const val junit = "org.jetbrains.kotlin:kotlin-test-junit:${versions.kotlin}"
        }
    }

    object rx {
        const val java = "io.reactivex.rxjava2:rxjava:2.2.13"
        const val android = "io.reactivex.rxjava2:rxandroid:2.1.1"
        const val binding = "com.jakewharton.rxbinding3:rxbinding:3.0.0"
    }

    object retrofit {
        const val core = "com.squareup.retrofit2:retrofit:${versions.retrofit}"
        const val moshi = "com.squareup.retrofit2:converter-moshi:${versions.retrofit}"
        const val logging = "com.squareup.okhttp3:logging-interceptor:4.1.0"
    }

    object detekt {
        const val plugin = "io.gitlab.arturbosch.detekt"
        const val lint = "io.gitlab.arturbosch.detekt:detekt-formatting:${versions.detekt}"
    }

    object repositories {
        const val m2 = "https://plugins.gradle.org/m2/"
    }

    object plugins {
        const val detekt = "io.gitlab.arturbosch.detekt"
        const val node = "com.moowork.node"
        const val mavenPublish = "maven-publish"
    }

    object classpaths {
        const val dokka = "org.jetbrains.dokka:dokka-gradle-plugin:0.9.18"
        const val bintray = "com.jfrog.bintray.gradle:gradle-bintray-plugin:${versions.bintray}"
    }

    object test {
        const val mockK = "io.mockk:mockk:1.9.3"
    }

    object libs {
        const val picasso = "com.squareup.picasso:picasso:2.71828"
    }
}