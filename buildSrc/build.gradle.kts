buildscript {
    repositories {
        jcenter()
    }
}

repositories {
    mavenCentral()
    google()
    jcenter()
    maven("https://plugins.gradle.org/m2/")
}

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.android.tools.build:gradle:3.5.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.50")
    implementation("com.github.node-gradle:gradle-node-plugin:2.2.0")
    implementation("org.codehaus.groovy:groovy:2.5.7")

    implementation(gradleApi())
}
