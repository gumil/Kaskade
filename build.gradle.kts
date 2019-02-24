import org.jlleitschuh.gradle.ktlint.KtlintPlugin

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
        classpath(deps.build.plugins.ktlint)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

subprojects {
    apply<KtlintPlugin>()

    afterEvaluate {
        apply { from(rootProject.file("gradle/jacoco.gradle.kts")) }
    }
}

val clean by tasks.creating(Delete::class) {
    delete(rootProject.buildDir)
}
