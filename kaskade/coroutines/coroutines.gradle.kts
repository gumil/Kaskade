plugins {
    kotlin("multiplatform")
    id(deps.plugins.node) version versions.node
    id(deps.bintray.plugin)
}

kotlin {
    jvm()
    js {
        configure(listOf(compilations["main"], compilations["test"])) {
            tasks.getByName(compileKotlinTaskName) {
                kotlinOptions {
                    metaInfo = true
                    sourceMap = true
                    sourceMapEmbedSources = "always"
                    moduleKind = "umd"
                }
            }
        }
    }
    iosX64()
    iosArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":core"))
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
                api(project(":core"))
                implementation(deps.kotlin.coroutines.native)
            }
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                api(project(":core"))
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
                api(project(":core"))
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
    from(rootProject.file("gradle/mpp-tests.gradle"))
    from(rootProject.file("gradle/maven-mpp.gradle"))
    from(rootProject.file("gradle/bintray.gradle"))
}
