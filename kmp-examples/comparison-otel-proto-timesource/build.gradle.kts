import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform") version "2.3.10"
    id("com.infendro.otel-proto-timesource") version "1.0.1"
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

val otelProtoTsMaxQueueSize = providers.gradleProperty("otelProtoTsMaxQueueSize")
    .map { it.toInt() }
    .getOrElse(2048)

val otelProtoTsMaxExportBatchSize = providers.gradleProperty("otelProtoTsMaxExportBatchSize")
    .map { it.toInt() }
    .getOrElse(512)

val otelProtoTsInstrumentPropertyAccessors = providers.gradleProperty("otelProtoTsInstrumentPropertyAccessors")
    .map { it.toBoolean() }
    .getOrElse(false)

otelProtoTimesource {
    host = "localhost:4317"
    service = "comparison-otel-proto-timesource"
    debug = true
    maxQueueSize = otelProtoTsMaxQueueSize
    maxExportBatchSize = otelProtoTsMaxExportBatchSize
    instrumentPropertyAccessors = otelProtoTsInstrumentPropertyAccessors
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
