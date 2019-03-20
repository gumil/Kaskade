plugins {
    id("kotlin")
}

apply { from(rootProject.file("gradle/kotlin-sources.gradle")) }

dependencies {
    implementation(project(":core"))

    implementation(deps.kotlin.stdlib.core)
    implementation(deps.rx.java)

    testImplementation(deps.kotlin.test.junit)
}

apply {
    from(rootProject.file("gradle/maven.gradle"))
    from(rootProject.file("gradle/bintray.gradle"))
}
