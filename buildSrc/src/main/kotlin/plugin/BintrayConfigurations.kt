package plugin

import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayPlugin
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.delegateClosureOf
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import java.util.Date

fun Project.configureBintray() {
    apply<BintrayPlugin>()

    extensions.getByType<BintrayExtension>().run {
        user = System.getenv("BINTRAY_USER")
        key = System.getenv("BINTRAY_KEY")
        publish = true
        dryRun = false

        pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
            userOrg = "gumil"
            repo = "maven"
            name = "kaskade"
            setLicenses("Apache-2.0")
            setPublications(*getPublicationNames())

            version(delegateClosureOf<BintrayExtension.VersionConfig> {
                val projectVersion = project.version as String
                name = projectVersion
                vcsTag = projectVersion
                released = Date().toString()

                gpg(delegateClosureOf<BintrayExtension.GpgConfig> {
                    sign = true
                    passphrase = System.getenv("BINTRAY_PASSPHRASE")
                })
            })
        })
    }

    if (plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
        tasks.named<BintrayUploadTask>("bintrayUpload") {
            doFirst {
                project.extensions.getByType<PublishingExtension>()
                    .publications
                    .filterIsInstance<MavenPublication>()
                    .forEach {
                        it.artifact("$buildDir/publications/${it.name}/module.json") {
                            extension = "module"
                        }
                    }
            }
        }
    }
}

private fun Project.getPublicationNames(): Array<String> {
    val osName = System.getenv("TRAVIS_OS_NAME") ?: ""

    val publishingExtension = extensions.getByType<PublishingExtension>()

    if (osName == "osx") {
        return publishingExtension.publications
            .map { it.name }
            .filter { it.contains("ios") }
            .toTypedArray()
    }

    return publishingExtension.publications
        .map { it.name }
        .toTypedArray()
}
