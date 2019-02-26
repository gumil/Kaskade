import io.gitlab.arturbosch.detekt.DetektPlugin

plugins { id(deps.build.plugins.detekt) version versions.detekt }

buildscript {
    repositories {
        google()
        jcenter()
        maven { url = uri(deps.build.repositories.plugins) }
    }
    dependencies {
        classpath(deps.android.build.gradlePlugin)
        classpath(deps.android.build.navigationPlugin)
        classpath(deps.kotlin.build.gradlePlugin)
        classpath(deps.build.plugins.androidMaven)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

subprojects {
    apply<DetektPlugin>()

    afterEvaluate {
        apply {
            from(rootProject.file("gradle/jacoco.gradle.kts"))
        }

        val configFile = if (project.displayName.contains("sample")) {
            "../detekt/comments-disabled.yml"
        } else {
            "../detekt/detekt.yml"
        }

        detekt {
            toolVersion = versions.detekt
            input = files("src/main/kotlin", "src/test/kotlin")
            config = files(configFile)
        }
        dependencies {
            detektPlugins(deps.detekt.lint)
        }
    }
}

val clean by tasks.creating(Delete::class) {
    delete(rootProject.buildDir)
}
