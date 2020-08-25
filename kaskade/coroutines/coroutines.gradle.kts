import com.android.build.gradle.internal.tasks.factory.dependsOn
import plugin.MultiplatformConfigurationPlugin

plugins {
    kotlin("multiplatform")
}

apply<MultiplatformConfigurationPlugin>()

kotlin {
    sourceSets {
        @kotlin.Suppress("UNUSED_VARIABLE")
        val commonMain by getting {
            dependencies {
                implementation(project(":core"))
                implementation(deps.kotlin.coroutines.core)
                implementation(kotlin("stdlib-common"))
            }
        }

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

tasks.named("jsTest").dependsOn(":core:compileTestKotlinJs")
