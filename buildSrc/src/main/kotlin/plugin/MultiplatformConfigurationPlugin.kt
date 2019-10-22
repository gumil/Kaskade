package plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation

class MultiplatformConfigurationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        configureMpp(project)
        project.configureMppTest()
    }

    private fun configureMpp(project: Project) {
        project.configurations.create("compileClasspath")

        project.extensions.getByType<KotlinMultiplatformExtension>().run {
            jvm()
            js {
                project.configure<KotlinJsCompilation>(listOf(compilations["main"], compilations["test"])) {
                    project.tasks.getByName(compileKotlinTaskName) {
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
        }
    }
}
