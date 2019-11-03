package plugin

import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

fun Project.configureMppTest() {
    configureIosTest(this)
    configureJsTest(this)

    tasks.register("test") {
        dependsOn("jvmTest", "iosTest", "jsTest")
        group = JavaBasePlugin.VERIFICATION_GROUP
    }

    repositories.whenObjectAdded {
        if (this is IvyArtifactRepository) {
            metadataSources {
                artifact()
            }
        }
    }
}

private fun configureIosTest(project: Project) {
    project.tasks.register("iosTest") {
        group = JavaBasePlugin.VERIFICATION_GROUP
        dependsOn("linkIosX64")
        val device = project.findProperty("iosDevice")?.toString() ?: "iPhone 8"

        doLast {
            val iosX64 = project.extensions.getByType<KotlinMultiplatformExtension>().iosX64()
            val binary = iosX64.binaries.getTest(NativeBuildType.DEBUG).outputFile
            project.exec {
                commandLine("xcrun", "simctl", "spawn", device, binary.absolutePath)
            }
        }
    }
}

private fun configureJsTest(project: Project) {
    project.apply<NodePlugin>()
    project.extensions.getByType<NodeExtension>().run {
        version = "10.9.0"
        download = true
        nodeModulesDir = project.file("${project.buildDir}/npm")
    }

    val installMocha = "installMocha"
    val populateNodeModules = "populateNodeModules"
    val runMocha = "runMocha"

    project.tasks.register<NpmTask>(installMocha)
    project.tasks.getByName(installMocha) {
        (this as NpmTask).apply {
            setArgs(listOf("install", "mocha@6.0.0"))
        }
    }

    val jsCompilations =
        project.extensions.getByType<KotlinMultiplatformExtension>().js().compilations
    val testCompilation = jsCompilations.getByName("test")

    project.tasks.register<Copy>(populateNodeModules) {
        from("${project.buildDir}/npm/node_modules")
        from(jsCompilations.getByName("main").output.allOutputs)

        testCompilation.runtimeDependencyFiles.forEach {
            if (it.exists() && !it.isDirectory) {
                from(project.zipTree(it.absolutePath).matching {
                    include("*.js")
                })
            }
        }

        into("${project.buildDir}/node_modules")
    }

    project.tasks.register<NodeTask>(runMocha) {
        dependsOn("compileTestKotlinJs", installMocha, populateNodeModules)
        script = project.file("${project.buildDir}/npm/node_modules/.bin/mocha")
        setArgs(
            listOf(
                "--timeout", "15000",
                project.file(
                    project.relativePath(testCompilation.output.allOutputs.first()) +
                        "/${project.name}_test.js"
                ).absolutePath
            )
        )
    }

    project.tasks.getByName("jsTest") {
        dependsOn(runMocha)
    }
}
