package plugin

import com.android.build.gradle.BaseExtension
import deps
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidConfigurationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.configureAndroid()
    }
}

class AndroidLibraryConfigurationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.configureAndroid()
        project.configureMavenAndroid()
    }
}

private fun Project.configureAndroid() = this.extensions.getByType<BaseExtension>().run {
    compileSdkVersion(deps.android.build.targetSdkVersion)
    buildToolsVersion(deps.android.build.buildToolsVersion)
    defaultConfig {
        minSdkVersion(deps.android.build.minSdkVersion)
        targetSdkVersion(deps.android.build.targetSdkVersion)
    }

    sourceSets {
        getByName("main").java.srcDir("src/main/kotlin")
        getByName("test").java.srcDir("src/test/kotlin")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
