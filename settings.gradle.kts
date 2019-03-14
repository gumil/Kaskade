include(
    ":kaskade",
    ":kaskade-rx",
    ":kaskade-livedata",
    ":kaskade-coroutines",
    ":sample-android",
    ":sample-kotlin"
)

rootProject.children.forEach {
    it.buildFileName = "${it.name}.gradle.kts"
}

enableFeaturePreview("GRADLE_METADATA")