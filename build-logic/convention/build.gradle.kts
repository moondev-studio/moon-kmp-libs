plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.agp.gradle.plugin)
    compileOnly(libs.compose.gradle.plugin)
    compileOnly(libs.dokka.gradle.plugin)
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
    }
}
