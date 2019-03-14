import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilationToRunnableFiles
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id(deps.build.plugins.node) version versions.node
    id(deps.build.plugins.mavenPublish)
}

group = "com.github.gumil"

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

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("stdlib-common"))
            }
        }

        val commonTest by getting {
            dependencies {
                api(kotlin("test-common"))
                api(kotlin("test-annotations-common"))
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

tasks.register("iosTest") {
    dependsOn("iosX64TestBinaries")
    doLast {
        val binary = (kotlin.targets["iosX64"] as KotlinNativeTarget)
            .compilations["test"]
            .getBinary("EXECUTABLE", "DEBUG")

        exec {
            commandLine("xcrun", "simctl", "spawn", "iPhone XR", binary.absolutePath)
        }
    }
}

node {
    version = "10.9.0"
    download = true
    nodeModulesDir = file("$buildDir/npm/")
}

tasks.register<NpmTask>("installMocha") {
    setWorkingDir(node.nodeModulesDir)
    setArgs(listOf("install", "mocha@6.0.0"))
}

val jsCompilations = kotlin.targets["js"].compilations

tasks.register("populateNodeModules") {
    doLast {
        copy {
        from("$buildDir/npm/node_modules")
        from(jsCompilations["main"].output.allOutputs)
        (jsCompilations["test"] as KotlinCompilationToRunnableFiles).runtimeDependencyFiles.forEach {
            if (it.exists() && !it.isDirectory) {
                from(zipTree(it.absolutePath).matching { include("*.js") })
            }
        }
        into("$buildDir/node_modules")
        }
    }
}

tasks.register<NodeTask>("runMocha") {
    dependsOn("compileTestKotlinJs", "installMocha", "populateNodeModules")
    setScript(file("$buildDir/npm/node_modules/.bin/mocha"))
    setArgs(listOf(
        "--timeout", "15000",
        relativePath(jsCompilations["test"].output.allOutputs.first()) + "/${project.name}_test.js"
    ))
}

tasks["jsTest"].dependsOn("runMocha")