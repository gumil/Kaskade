/**
 * Exclude sample modules
 */
if (!displayName.contains(":app") && !displayName.contains(":console")) {
    apply<JacocoPlugin>()

    extensions.getByType(JacocoPluginExtension::class.java).toolVersion = versions.jacoco

    val task = "jacocoTestReport"

    when {
        plugins.hasPlugin("com.android.library") -> {
            tasks.register<JacocoReport>(task) {
                setDependsOn(setOf("testDebugUnitTest", "createDebugCoverageReport"))
                group = "verification"
                description = "Runs jacoco test report for android"

                val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug")
                val mainSrc = "${project.projectDir}/src/main/kotlin"

                sourceDirectories.setFrom(files(mainSrc))
                classDirectories.setFrom(files(debugTree))

                executionData.setFrom(fileTree(buildDir).apply {
                    setIncludes(setOf("jacoco/testDebugUnitTest.exec", "outputs/code-coverage/connected/*coverage.ec"))
                })
            }
        }
        plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") -> {
            tasks.register<JacocoReport>(task) {
                setDependsOn(setOf("jvmTest"))
                group = "verification"
                description = "Runs jacoco test report for jvm multiplatform"

                val debugTree = fileTree("${project.buildDir}/classes/kotlin/jvm/")
                val mainSrc = "${project.projectDir}/src/commonMain/kotlin"

                sourceDirectories.setFrom(files(mainSrc))
                classDirectories.setFrom(files(debugTree))

                executionData.setFrom(fileTree(buildDir).apply {
                    setIncludes(setOf("jacoco/jvmTest.exec"))
                })
            }
        }
        else -> {
            tasks.named<JacocoReport>(task).configure {
                dependsOn(tasks.named("test"))
            }
        }
    }

    tasks.named<JacocoReport>(task).configure {
        reports.apply {
            xml.apply {
                isEnabled = true
                destination = File("${project.buildDir}/reports/jacocoTestReport.xml")
            }
            html.apply {
                isEnabled = true
                destination = File("${project.buildDir}/reports/jacoco")
            }
        }
    }
}
