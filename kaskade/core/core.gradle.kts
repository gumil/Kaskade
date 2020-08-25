import plugin.MultiplatformConfigurationPlugin

plugins {
    kotlin("multiplatform")
}

apply<MultiplatformConfigurationPlugin>()

kotlin {
    sourceSets {

        @kotlin.Suppress("UNUSED_VARIABLE")
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        @kotlin.Suppress("UNUSED_VARIABLE")
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        @kotlin.Suppress("UNUSED_VARIABLE")
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
