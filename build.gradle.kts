plugins {
    id("io.gitlab.arturbosch.detekt") version versions.detekt
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
}

subprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
    }

    detekt {
        toolVersion = versions.detekt
        input = files("src/main/kotlin", "src/test/kotlin",
            "src/commonMain/kotlin", "src/commonTest/kotlin")
        config = files("$rootDir/detekt/detekt.yml")
    }

    dependencies {
        detektPlugins(deps.detekt.lint)
    }
}

val cleanAll by tasks.creating(Delete::class) {
    delete(rootProject.buildDir)
}
