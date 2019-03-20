include(":core")
include(":rx")
include(":livedata")
include(":coroutines")
include(":sample-android")
include(":sample-kotlin")

project(":core").projectDir = file("kaskade/core")
project(":rx").projectDir = file("kaskade/rx")
project(":livedata").projectDir = file("kaskade/livedata")
project(":coroutines").projectDir = file("kaskade/coroutines")

rootProject.children.forEach {
    it.buildFileName = "${it.name}.gradle.kts"
}

enableFeaturePreview("GRADLE_METADATA")