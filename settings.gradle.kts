include("steps")
include("steps-server")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        maven(url = "https://repo.maven.apache.org/maven2/")
        google()
    }
}