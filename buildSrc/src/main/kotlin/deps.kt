@file:Suppress("unused", "ClassName")

object versions {
    const val jacoco = "0.8.3"
    const val detekt = "1.0.0-RC12"
    const val node = "1.2.0"

    internal const val kotlin = "1.3.21"
    internal const val navigation = "2.0.0-rc02"
    internal const val coroutines = "1.1.0"
    internal const val lifecycle = "2.0.0"
    internal const val retrofit = "2.5.0"
    internal const val bintray = "1.8.4"
}

object deps {
    object android {
        object build {
            const val buildToolsVersion = "28.0.3"
            const val compileSdkVersion = 28
            const val minSdkVersion = 16
            const val sampleMinSdkVersion = 21
            const val targetSdkVersion = 28
        }

        const val classpath = "com.android.tools.build:gradle:3.3.0"

        object lifecycle {
            const val livedata = "androidx.lifecycle:lifecycle-livedata:${versions.lifecycle}"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:${versions.lifecycle}"
            const val test = "androidx.arch.core:core-testing:${versions.lifecycle}"
        }

        const val appCompat = "androidx.appcompat:appcompat:1.0.2"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.0.0"
        const val material = "com.google.android.material:material:1.1.0-alpha02"
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
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${versions.coroutines}"
        }

        object test {
            const val junit = "org.jetbrains.kotlin:kotlin-test-junit:${versions.kotlin}"
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

    object bintray {
        const val plugin = "com.jfrog.bintray"
        const val classpath = "com.jfrog.bintray.gradle:gradle-bintray-plugin:${versions.bintray}"
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
        const val dokka = "org.jetbrains.dokka:dokka-gradle-plugin:0.9.17"
    }

    object test {
        const val mockK = "io.mockk:mockk:1.9.1"
    }

    object libs {
        const val picasso = "com.squareup.picasso:picasso:2.71828"
    }
}