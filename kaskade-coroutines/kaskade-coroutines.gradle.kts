plugins {
    id("kotlin")
    id(deps.bintray.plugin)
}

apply { from(rootProject.file("gradle/kotlin-sources.gradle")) }

dependencies {
    implementation(project(":kaskade"))

    implementation(deps.kotlin.stdlib.core)
    implementation(deps.kotlin.coroutines.core)

    testImplementation(deps.test.kotlinjUnit)
    testImplementation(deps.test.mockK)
}

apply {
    from(rootProject.file("gradle/maven.gradle"))
    from(rootProject.file("gradle/bintray.gradle"))
}
