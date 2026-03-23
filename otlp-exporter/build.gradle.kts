plugins {
    kotlin("multiplatform") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("maven-publish")
}

group = "com.infendro.otel"
version = "1.0.0"

repositories {
    maven {
        url = uri("https://maven.pkg.github.com/dcxp/opentelemetry-kotlin")
        credentials {
            username = project.property("GITHUB_USERNAME") as String
            password = project.property("GITHUB_PASSWORD") as String
        }
    }
    mavenCentral()
}

kotlin {
    jvm()
    js {
        nodejs()
        useCommonJs()
    }
    linuxX64()

    sourceSets {
        commonMain.dependencies {
            implementation("io.opentelemetry.kotlin.api:all:1.0.570")
            implementation("io.opentelemetry.kotlin.sdk:sdk-trace:1.0.570")

            implementation("io.ktor:ktor-client-core:3.1.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
        }
        jvmMain.dependencies {
            implementation("io.ktor:ktor-client-java:3.1.0")
        }
        jsMain.dependencies {
            implementation("io.ktor:ktor-client-js:3.1.0")
        }
        linuxMain.dependencies {
            implementation("io.ktor:ktor-client-curl:3.1.0")
        }
    }
}
