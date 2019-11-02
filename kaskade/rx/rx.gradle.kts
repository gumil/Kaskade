import plugin.JvmLibraryConfigurationPlugin

plugins {
    id("kotlin")
}

apply<JvmLibraryConfigurationPlugin>()

dependencies {
    implementation(project(":core"))

    implementation(deps.kotlin.stdlib.core)
    implementation(deps.rx.java)

    testImplementation(deps.kotlin.test.junit)
}
