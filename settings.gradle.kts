include(":core")
include(":coroutines")

project(":core").projectDir = file("kaskade/core")
project(":coroutines").projectDir = file("kaskade/coroutines")

if (System.getenv("TRAVIS_OS_NAME") != "osx") {
    include(":rx")
    include(":livedata")
    include(":app")
    include(":console")

    project(":rx").projectDir = file("kaskade/rx")
    project(":livedata").projectDir = file("kaskade/livedata")
    project(":app").projectDir = file("sample/app")
    project(":console").projectDir = file("sample/console")
}

rootProject.children.forEach {
    it.buildFileName = "${it.name}.gradle.kts"
}

enableFeaturePreview("GRADLE_METADATA")
