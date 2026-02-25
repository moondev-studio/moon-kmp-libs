plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.agp.gradle.plugin)
    compileOnly(libs.compose.gradle.plugin)
    compileOnly(libs.dokka.gradle.plugin)
    compileOnly(libs.maven.publish.plugin)
}

gradlePlugin {
    plugins {
        register("moonKmpLibrary") {
            id = "moon.kmp.library"
            implementationClass = "MoonKmpLibraryPlugin"
        }
        register("moonComposeLibrary") {
            id = "moon.compose.library"
            implementationClass = "MoonComposeLibraryPlugin"
        }
        register("moonPublish") {
            id = "moon.publish"
            implementationClass = "MoonPublishPlugin"
        }
    }
}
