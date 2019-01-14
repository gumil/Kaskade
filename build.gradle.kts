import org.jlleitschuh.gradle.ktlint.KtlintPlugin

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra["kotlin_version"] = "1.3.11"
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

    apply<KtlintPlugin>()
}

val clean by tasks.creating(Delete::class) {
    delete(rootProject.buildDir)
}
