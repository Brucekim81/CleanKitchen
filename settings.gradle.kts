pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        repositories {
            maven { setUrl("https://jitpack.io") }
        }
        //maven { url 'https://jitpack.io' }
    }
}

rootProject.name = "CleaningCuisine"
include(":app")
 