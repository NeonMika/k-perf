import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform") version "2.1.10"
}

group = "com.infendro.otel.measure"
version = "1.0.0"

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
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        mainRun {
            mainClass.set("MainKt")
        }
        compilations.all {
            tasks.withType<Jar> {
                doFirst {
                    manifest {
                        attributes(
                            "Main-Class" to "MainKt",
                            "Class-Path" to runtimeDependencyFiles.files.joinToString(" ") { it.name })
                    }
                }
                doLast {
                    copy {
                        duplicatesStrategy = DuplicatesStrategy.INCLUDE
                        from("build/libs")
                        from(runtimeDependencyFiles.files)
                        into("build/lib")
                    }
                }
            }
        }
    }
    js {
        nodejs()
        useCommonJs()
        binaries.executable()
    }
    linuxX64 {
        binaries.executable {
            entryPoint = "main"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation("io.opentelemetry.kotlin.api:all:1.0.570")
            implementation("io.opentelemetry.kotlin.sdk:sdk-trace:1.0.570")
            implementation("com.infendro.otel:otlp-exporter:1.0.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
        }
        jsMain.dependencies {
            implementation("org.jetbrains.kotlin-wrappers:kotlin-node:2025.4.7-22.13.10")
        }
    }
}
