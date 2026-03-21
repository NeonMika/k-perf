// needed so that the k-perf-measure-plugin plugin can be used in this project from mavenLocal
pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenLocal()
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "comparison-k-perf"
