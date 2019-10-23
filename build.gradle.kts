import io.gitlab.arturbosch.detekt.DetektPlugin

plugins { id(deps.detekt.plugin) version versions.detekt }

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath(deps.classpaths.dokka)
        classpath(deps.classpaths.bintray)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
}

subprojects {
    apply<DetektPlugin>()

    afterEvaluate {
        apply {
            from(rootProject.file("gradle/jacoco.gradle.kts"))
        }

        val configFile = "$rootDir/detekt/detekt.yml"

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
