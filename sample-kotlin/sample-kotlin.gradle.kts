plugins { id("kotlin") }

apply { from(rootProject.file("gradle/kotlin-sources.gradle")) }

dependencies {
    implementation(project(":kaskade"))

    implementation(deps.kotlin.stdlib.core)

    testImplementation(deps.kotlin.test.junit)
    testImplementation(deps.test.mockK)
}
