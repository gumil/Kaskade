plugins {
    id("kotlin")
    id("com.github.dcendents.android-maven")
}

apply { from(rootProject.file("gradle/kotlin-sources.gradle")) }

dependencies {
    implementation(project(":kaskade"))

    implementation(deps.kotlin.stdlib.core)
    implementation(deps.kotlin.coroutines.core)

    testImplementation(deps.test.kotlinjUnit)
}
