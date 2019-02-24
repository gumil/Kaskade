/**
 * Exclude sample modules
 */
if (!displayName.contains("sample")) {
    apply<JacocoPlugin>()

    plugins.getPlugin(JacocoPlugin::class.java).apply {
        version = versions.jacoco
    }

    if (plugins.hasPlugin("com.android.library")) {
        tasks.register<JacocoReport>("jacocoTestReport") {
            setDependsOn(setOf("testDebugUnitTest", "createDebugCoverageReport"))
            group = "verification"
            description = "Runs jacoco test report for android"

            val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug")
            val mainSrc = "${project.projectDir}/src/main/kotlin"

            sourceDirectories = files(mainSrc)
            classDirectories = files(debugTree)

            executionData = fileTree(buildDir).apply {
                setIncludes(setOf("jacoco/testDebugUnitTest.exec", "outputs/code-coverage/connected/*coverage.ec"))
            }
        }
    } else {
        tasks.named<JacocoReport>("jacocoTestReport").configure {
            dependsOn(tasks.named("test"))
        }
    }

    tasks.named<JacocoReport>("jacocoTestReport").configure {
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