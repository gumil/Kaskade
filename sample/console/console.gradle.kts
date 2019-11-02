import plugin.JvmConfigurationPlugin

plugins { id("kotlin") }

apply<JvmConfigurationPlugin>()

dependencies {
    implementation(project(":core"))

    implementation(deps.kotlin.stdlib.core)

    testImplementation(deps.kotlin.test.junit)
    testImplementation(deps.test.mockK)
}
