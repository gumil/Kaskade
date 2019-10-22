buildscript {
    repositories {
        jcenter()
    }
}

repositories {
    mavenCentral()
    google()
    jcenter()
}

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.android.tools.build:gradle:3.5.1")

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.50")

    implementation(gradleApi())
    implementation(localGroovy())
}
