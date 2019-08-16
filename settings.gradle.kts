include(":core")
include(":rx")
include(":livedata")
include(":coroutines")
include(":app")
include(":console")

project(":core").projectDir = file("kaskade/core")
project(":rx").projectDir = file("kaskade/rx")
project(":livedata").projectDir = file("kaskade/livedata")
project(":coroutines").projectDir = file("kaskade/coroutines")
project(":app").projectDir = file("sample/app")
project(":console").projectDir = file("sample/console")

rootProject.children.forEach {
    it.buildFileName = "${it.name}.gradle.kts"
}

enableFeaturePreview("GRADLE_METADATA")
