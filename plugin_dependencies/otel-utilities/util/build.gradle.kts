plugins {
    kotlin("multiplatform") version "2.3.10"
    id("maven-publish")
}

group = "com.infendro.otel"
version = "1.0.1"

repositories {
    maven {
        url = uri("https://maven.pkg.github.com/dcxp/opentelemetry-kotlin")
        credentials {
            username = project.property("GITHUB_USERNAME") as String
            password = project.property("GITHUB_PASSWORD") as String
        }
    }
    mavenLocal()
    mavenCentral()
}

kotlin {
    applyDefaultHierarchyTemplate()

    jvm()
    js {
        nodejs()
        useCommonJs()
    }
    linuxX64()
    mingwX64()

    sourceSets {
        commonMain {
            kotlin.srcDir(rootProject.file("src/commonMain/kotlin"))
            dependencies {
                implementation("io.opentelemetry.kotlin.api:all:1.0.570")
                implementation("io.opentelemetry.kotlin.sdk:sdk-trace:1.0.570")
                implementation("com.infendro.otel:otlp-exporter:1.0.1")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            }
        }
        jvmMain {
            kotlin.srcDir(rootProject.file("src/jvmMain/kotlin"))
        }
        jsMain {
            kotlin.srcDir(rootProject.file("src/jsMain/kotlin"))
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-node:2025.4.7-22.13.10")
            }
        }
        nativeMain {
            kotlin.srcDir(rootProject.file("src/nativeMain/kotlin"))
        }
    }
}
