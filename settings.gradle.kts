pluginManagement {
    repositories {
        google()            // Necesario para plugins de Google como google-services
        gradlePluginPortal() // Plugins de Gradle
        mavenCentral()      // Maven central
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Proyecto_Pasteleria_Mil_Sabores"
include(":app")
