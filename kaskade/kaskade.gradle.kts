plugins {
    id("kotlin")
    id("com.github.dcendents.android-maven")
}

apply { from(rootProject.file("gradle/kotlin-sources.gradle")) }

dependencies {
    implementation(deps.kotlin.stdlib.core)

    testImplementation(deps.test.kotlinjUnit)
    testImplementation(deps.test.mockK)
}
