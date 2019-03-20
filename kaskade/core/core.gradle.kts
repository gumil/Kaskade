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
                implementation(kotlin("stdlib-common"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        // Default source set for JVM-specific sources and dependencies:
        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        // JVM-specific tests and their dependencies:
        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        js().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        js().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

apply {
    from(rootProject.file("gradle/mpp-tests.gradle"))
    from(rootProject.file("gradle/maven-mpp.gradle"))
    from(rootProject.file("gradle/bintray.gradle"))
}
