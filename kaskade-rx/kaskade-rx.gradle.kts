plugins {
    id("kotlin")
    id(deps.bintray.plugin)
}

apply { from(rootProject.file("gradle/kotlin-sources.gradle")) }

dependencies {
    implementation(project(":kaskade"))

    implementation(deps.kotlin.stdlib.core)
    implementation(deps.rx.java)

    testImplementation(deps.test.kotlinjUnit)
}

apply {
    from(rootProject.file("gradle/maven.gradle"))
    from(rootProject.file("gradle/bintray.gradle"))
}
