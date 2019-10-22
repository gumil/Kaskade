import com.android.build.gradle.internal.tasks.factory.dependsOn
import plugin.MultiplatformConfigurationPlugin

plugins {
    kotlin("multiplatform")
}

apply<MultiplatformConfigurationPlugin>()

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core"))
                implementation(deps.kotlin.coroutines.common)
                implementation(kotlin("stdlib-common"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val nativeMain by creating {
            dependencies {
                implementation(deps.kotlin.coroutines.native)
            }
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(deps.kotlin.coroutines.core)
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        js().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(deps.kotlin.coroutines.js)
                implementation(kotlin("stdlib-js"))
            }
        }
        js().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }

    configure(listOf(iosX64(), iosArm64())) {
        compilations["main"].source(sourceSets["nativeMain"])
    }
}

apply {
    from(rootProject.file("gradle/maven-mpp.gradle"))
    from(rootProject.file("gradle/bintray.gradle"))
}

tasks.named("jsTest").dependsOn(":core:compileTestKotlinJs")
