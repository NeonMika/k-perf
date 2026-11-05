import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform") version "2.3.10"
    id("io.github.neonmika.k-perf-otel-proto-anchored-plugin") version "0.2.1"
}

group = "at.jku.ssw"
version = "0.1.0"

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

val kperfOtelProtoAnchoredMaxQueueSize = providers.gradleProperty("kperfOtelProtoAnchoredMaxQueueSize")
    .map { it.toInt() }
    .getOrElse(2048)

val kperfOtelProtoAnchoredMaxExportBatchSize = providers.gradleProperty("kperfOtelProtoAnchoredMaxExportBatchSize")
    .map { it.toInt() }
    .getOrElse(512)

val kperfOtelProtoAnchoredInstrumentPropertyAccessors = providers.gradleProperty("kperfOtelProtoAnchoredInstrumentPropertyAccessors")
    .map { it.toBoolean() }
    .getOrElse(false)

kperfOtelProtoAnchored {
    host = "localhost:4317"
    service = "fibonacci-k-perf-otel-proto-anchored"
    debug = true
    maxQueueSize = kperfOtelProtoAnchoredMaxQueueSize
    maxExportBatchSize = kperfOtelProtoAnchoredMaxExportBatchSize
    instrumentPropertyAccessors = kperfOtelProtoAnchoredInstrumentPropertyAccessors
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
    @OptIn(org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalMainFunctionArgumentsDsl::class)
    js {
        nodejs {
            passProcessArgvToMainFunction()
        }
        useCommonJs()
        binaries.executable()
    }
    macosX64 {
        binaries.executable {
            entryPoint = "main"
            baseName = "main"
        }
    }
    linuxX64 {
        binaries.executable {
            entryPoint = "main"
            baseName = "main"
        }
    }
    mingwX64 {
        binaries.executable {
            entryPoint = "main"
            baseName = "main"
        }
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(npm("@js-joda/core", "3.2.0"))
            }
        }
    }
}
