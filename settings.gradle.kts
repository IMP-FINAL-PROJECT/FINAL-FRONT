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
        maven(uri("https://jitpack.io"))
        maven(uri("https://devrepo.kakao.com/nexus/repository/kakaomap-releases/"))
    }
}

rootProject.name = "FluffyMood"
include(":app")
include(":data")
include(":presentation")
include(":domain")
