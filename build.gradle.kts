import plugin.DetektConfigurationPlugin

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
}

subprojects {
    apply<DetektConfigurationPlugin>()
}

val clean by tasks.creating(Delete::class) {
    delete(rootProject.buildDir)
}
