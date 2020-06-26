@file:Suppress("unused", "ClassName")

object versions {
    const val detekt = "1.10.0-RC1"
    internal const val jacoco = "0.8.5"

    internal const val kotlin = "1.3.72"
    internal const val navigation = "2.3.0"
    internal const val coroutines = "1.3.7"
    internal const val lifecycle = "2.2.0"
    internal const val retrofit = "2.9.0"
}

object deps {
    object android {
        object build {
            const val buildToolsVersion = "29.0.3"
            const val compileSdkVersion = 29
            const val minSdkVersion = 16
            const val sampleMinSdkVersion = 23
            const val targetSdkVersion = 29
        }

        object lifecycle {
            const val livedata = "androidx.lifecycle:lifecycle-livedata:${versions.lifecycle}"
            const val test = "androidx.arch.core:core-testing:2.1.0"
        }
    }

    object kotlin {
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
        const val java = "io.reactivex.rxjava2:rxjava:2.2.19"
    }

    object detekt {
        const val lint = "io.gitlab.arturbosch.detekt:detekt-formatting:${versions.detekt}"
    }

    object test {
        const val mockK = "io.mockk:mockk:1.10.0"
    }

    /**
     * Dependencies used by the sample app
     */
    object sample {
        const val picasso = "com.squareup.picasso:picasso:2.71828"

        object retrofit {
            const val core = "com.squareup.retrofit2:retrofit:${versions.retrofit}"
            const val moshi = "com.squareup.retrofit2:converter-moshi:${versions.retrofit}"
            const val logging = "com.squareup.okhttp3:logging-interceptor:4.7.2"
        }

        object rx {
            const val android = "io.reactivex.rxjava2:rxandroid:2.1.1"
            const val binding = "com.jakewharton.rxbinding3:rxbinding:3.1.0"
        }

        object lifecycle {
            const val extensions = "androidx.lifecycle:lifecycle-extensions:${versions.lifecycle}"
        }

        object android {
            const val appCompat = "androidx.appcompat:appcompat:1.1.0"
            const val recyclerView = "androidx.recyclerview:recyclerview:1.1.0"
            const val material = "com.google.android.material:material:1.3.0-alpha01"
            const val navigation = "androidx.navigation:navigation-fragment:${versions.navigation}"
            const val fragment = "androidx.fragment:fragment-ktx:1.2.5"
        }
    }
}
