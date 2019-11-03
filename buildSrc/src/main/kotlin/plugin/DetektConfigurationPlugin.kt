package plugin

import deps
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.detekt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import versions

class DetektConfigurationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.apply<DetektPlugin>()

        project.afterEvaluate {
            val configFile = "$rootDir/detekt/detekt.yml"

            detekt {
                toolVersion = versions.detekt
                input = files("src/main/kotlin", "src/test/kotlin",
                    "src/commonMain/kotlin", "src/commonTest/kotlin")
                config = files(configFile)
            }

            dependencies {
                add("detektPlugins", deps.detekt.lint)
            }
        }
    }
}
