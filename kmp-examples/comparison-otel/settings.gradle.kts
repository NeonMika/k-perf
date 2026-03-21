pluginManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/dcxp/opentelemetry-kotlin")
            credentials {
                username = providers.gradleProperty("GITHUB_USERNAME").get()
                password = providers.gradleProperty("GITHUB_PASSWORD").get()
            }
        }
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "comparison-otel"
