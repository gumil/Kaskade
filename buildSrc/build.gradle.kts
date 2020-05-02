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
    implementation("com.android.tools.build:gradle:3.6.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
    implementation("com.github.node-gradle:gradle-node-plugin:2.2.3")
    implementation("org.codehaus.groovy:groovy:3.0.3")
    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.8.0")

    implementation(gradleApi())
}
