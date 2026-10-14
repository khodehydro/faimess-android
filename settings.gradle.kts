pluginManagement {
    repositories {
        // Resolve plugin markers (including AGP) through Myket before the upstream repositories.
        maven {
            name = "MyketMirror"
            url = uri("https://maven.myket.ir/")
        }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Use the same mirror for AndroidX, Compose, Kotlin and other library dependencies.
        maven {
            name = "MyketMirror"
            url = uri("https://maven.myket.ir/")
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "Faimess"
include(":app")
