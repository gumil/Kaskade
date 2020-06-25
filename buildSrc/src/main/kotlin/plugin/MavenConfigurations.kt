package plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

fun Project.configureMavenMultiplatform() {
    apply<MavenPublishPlugin>()

    extensions.getByType<PublishingExtension>().run {
        publications.filterIsInstance<MavenPublication>().forEach {
            it.groupId = project.group as String
            it.version = project.version as String
            withPom(it.pom)
        }
    }
}

fun Project.configureMavenAndroid() {
    createSourcesJarTask {
        dependsOn("assemble")
        from(
            project.extensions.getByType<BaseExtension>()
                .sourceSets.getByName("main").java.srcDirs
        )
    }

    configureMaven {
        artifact("$buildDir/outputs/aar/${project.name}-release.aar")
    }
}

fun Project.configureMavenJvm() {
    createSourcesJarTask {
        from(
            project.extensions.getByType<SourceSetContainer>()
                .getByName("main").java.srcDirs
        )
    }

    configureMaven {
        from(components.getByName("java"))
    }
}

private fun Project.createSourcesJarTask(configure: Jar.() -> Unit) {
    tasks.register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        this.configure()
    }
}

private fun Project.configureMaven(configuration: MavenPublication.() -> Unit = {}) {
    apply<MavenPublishPlugin>()

    extensions.getByType<PublishingExtension>().run {
        publications.create<MavenPublication>("mavenProject") {
            groupId = project.group as String
            version = project.version as String
            artifactId = project.name
            artifact(tasks.getByName("sourcesJar"))
            configuration()
            withPom(pom)
        }
    }
}

private fun Project.withPom(pom: MavenPom) {
    pom.withXml {
        val root = asNode()
        root.appendNode("name", project.name)
        root.appendNode("description", "Unidirectional state container for Kotlin")
        root.appendNode("url", "https://github.com/gumil/Kaskade/")

        root.appendNode("licenses").apply {
            appendNode("license").apply {
                appendNode("name", "The Apache Software License, Version 2.0")
                appendNode("url", "http://www.apache.org/licenses/LICENSE-2.0.txt")
                appendNode("distribution", "repo")
            }
        }

        root.appendNode("developers").apply {
            appendNode("developer").apply {
                appendNode("id", "gumil")
                appendNode("name", "Miguel Panelo")
                appendNode("organizationUrl", "https://github.com/gumil/")
            }
        }
    }
}
