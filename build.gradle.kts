import io.gitlab.arturbosch.detekt.DetektPlugin

plugins { id(deps.detekt.plugin) version versions.detekt }

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
