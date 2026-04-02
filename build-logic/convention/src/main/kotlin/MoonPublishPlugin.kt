import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure

class MoonPublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.vanniktech.maven.publish")

            configure<MavenPublishBaseExtension> {
                publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

                // Sign only when GPG key is available (CI or local with gpg agent)
                // vanniktech in-memory signing: ORG_GRADLE_PROJECT_signingInMemoryKey
                val hasGpgKey = System.getenv("GPG_PRIVATE_KEY")?.isNotBlank() == true ||
                    System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey")?.isNotBlank() == true ||
                    findProperty("signingInMemoryKey") != null
                if (hasGpgKey) {
                    signAllPublications()
                }

                pom {
                    name.set(project.name)
                    description.set("Kotlin Multiplatform library by MoonDeveloper")
                    url.set("https://github.com/moondev-studio/moon-kmp-libs")

                    licenses {
                        license {
                            name.set("Apache License 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }

                    developers {
                        developer {
                            id.set("moondev-studio")
                            name.set("MoonDeveloper")
                        }
                    }

                    scm {
                        url.set("https://github.com/moondev-studio/moon-kmp-libs")
                        connection.set("scm:git:git://github.com/moondev-studio/moon-kmp-libs.git")
                        developerConnection.set("scm:git:ssh://github.com/moondev-studio/moon-kmp-libs.git")
                    }
                }
            }

            // Add GitHub Packages as secondary publish target
            pluginManager.withPlugin("maven-publish") {
                configure<PublishingExtension> {
                    repositories {
                        maven {
                            name = "GitHubPackages"
                            url = uri("https://maven.pkg.github.com/moondev-studio/moon-kmp-libs")
                            credentials {
                                username = findProperty("gpr.user") as String?
                                    ?: System.getenv("GITHUB_ACTOR")
                                password = findProperty("gpr.key") as String?
                                    ?: System.getenv("GH_PAT")
                            }
                        }
                    }
                }
            }
        }
    }
}
