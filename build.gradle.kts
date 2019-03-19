import io.gitlab.arturbosch.detekt.DetektPlugin

plugins { id(deps.detekt.plugin) version versions.detekt }

buildscript {
    repositories {
        google()
        jcenter()
        maven { url = uri(deps.repositories.m2) }
    }
    dependencies {
        classpath(deps.android.classpath)
        classpath(deps.kotlin.classpath)
        classpath(deps.classpaths.dokka)
        classpath(deps.bintray.classpath)
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
            input = files("src/main/kotlin", "src/test/kotlin",
                "src/commonMain/kotlin", "src/commonTest/kotlin")
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
