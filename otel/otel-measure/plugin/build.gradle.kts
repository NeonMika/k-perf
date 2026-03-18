import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform") version "2.1.10"
    id("com.infendro.otel") version "1.0.0"
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

otel {
    host = "localhost:4318"
    service = "plugin"
    debug = true
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
}
