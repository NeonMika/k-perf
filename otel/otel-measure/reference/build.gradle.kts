import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform") version "2.1.10"
}

group = "com.infendro.otel.measure"
version = "1.0.0"

repositories {
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
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
        }
    }
}
