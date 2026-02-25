import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MoonKmpLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.dokka")
            pluginManager.apply("moon.publish")

            configure<KotlinMultiplatformExtension> {
                androidTarget {
                    compilerOptions {
                        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
                    }
                }
                iosX64()
                iosArm64()
                iosSimulatorArm64()
                jvm("desktop")

                sourceSets.apply {
                    commonMain.dependencies { }
                    commonTest.dependencies {
                        implementation(libs.findLibrary("kotlin-test").get())
                    }
                }
            }

            configure<LibraryExtension> {
                val moduleName = target.name
                    .removePrefix("moon-")
                    .removeSuffix("-kmp")
                    .replace("-", ".")
                namespace = "com.moondeveloper.$moduleName"
                compileSdk = 36
                defaultConfig { minSdk = 24 }
                compileOptions {
                    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_17
                    targetCompatibility = org.gradle.api.JavaVersion.VERSION_17
                }
            }
        }
    }
}

private val Project.libs
    get() = extensions.getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java)
        .named("libs")
