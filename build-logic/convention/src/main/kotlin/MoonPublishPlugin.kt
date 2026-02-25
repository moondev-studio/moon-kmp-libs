import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class MoonPublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.vanniktech.maven.publish")

            configure<MavenPublishBaseExtension> {
                publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
                signAllPublications()

                coordinates(
                    groupId = "com.moondeveloper",
                    artifactId = project.name,
                    version = "1.0.0-SNAPSHOT"
                )

                pom {
                    name.set(project.name)
                    description.set("Kotlin Multiplatform library by MoonDeveloper")
                    url.set("https://github.com/sun941003/moon-kmp-libs")

                    licenses {
                        license {
                            name.set("Apache License 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }

                    developers {
                        developer {
                            id.set("moondeveloper")
                            name.set("MoonDeveloper")
                        }
                    }

                    scm {
                        url.set("https://github.com/sun941003/moon-kmp-libs")
                        connection.set("scm:git:git://github.com/sun941003/moon-kmp-libs.git")
                        developerConnection.set("scm:git:ssh://github.com/sun941003/moon-kmp-libs.git")
                    }
                }
            }
        }
    }
}
