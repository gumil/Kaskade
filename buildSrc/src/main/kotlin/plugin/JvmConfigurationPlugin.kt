package plugin

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.getByType

class JvmConfigurationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.configureJvm()
    }
}

class JvmLibraryConfigurationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.configureJvm()
        project.configureMavenJvm()
        project.configureBintray()
    }
}

private fun Project.configureJvm() {
    project.extensions.getByType<SourceSetContainer>().run {
        getByName("main").java.srcDir("src/main/kotlin")
        getByName("test").java.srcDir("src/test/kotlin")
    }

    project.extensions.getByType<JavaPluginExtension>().run {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
