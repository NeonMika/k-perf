// needed so that the k-perf-measure-plugin plugin can be used in this project from mavenLocal
pluginManagement {
  repositories {
    mavenLocal() // Add this line to include mavenLocal()
    gradlePluginPortal()
    google()
    mavenCentral()
    // Add any other repositories you're currently using
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenLocal() // Add this line to include mavenLocal()
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

plugins {
  // Apply the foojay-resolver plugin to allow automatic download of JDKs
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "kotlin-multiplatform-game-of-life"