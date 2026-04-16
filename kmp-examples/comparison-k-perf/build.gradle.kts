@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

plugins {
  kotlin("multiplatform") version "2.3.10"
  id("at.jku.ssw.k-perf-plugin") version "0.1.0"
}

group = "at.jku.ssw"
version = "0.1.0"

repositories {
  mavenCentral()
  mavenLocal()
}

val kperfFlushEarly = providers.gradleProperty("kperfFlushEarly")
  .map { it.toBoolean() }
  .getOrElse(false)

val kperfInstrumentPropertyAccessors = providers.gradleProperty("kperfInstrumentPropertyAccessors")
  .map { it.toBoolean() }
  .getOrElse(false)

val kperfTestKIR = providers.gradleProperty("kperfTestKIR")
  .map { it.toBoolean() }
  .getOrElse(false)

kperf {
  enabled = true
  flushEarly = kperfFlushEarly
  instrumentPropertyAccessors = kperfInstrumentPropertyAccessors
  testKIR = kperfTestKIR
}

val outputSuffix = "flushEarly-$kperfFlushEarly"

kotlin {
  jvm {
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
        archiveClassifier.set(outputSuffix)
        doLast {
          copy {
            from("build/libs")
            from(runtimeDependencyFiles.files)
            into("build/lib")
          }
        }
      }
    }
  }
  
  @OptIn(org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalMainFunctionArgumentsDsl::class)
  js(IR) {
    outputModuleName.set("${project.name}-$outputSuffix")
    nodejs {
      passProcessArgvToMainFunction()
    }
    binaries.executable()
  }

  val macosX64 = macosX64()
  val linuxX64 = linuxX64()
  val mingwX64 = mingwX64()

  listOf(
    macosX64,
    linuxX64,
    mingwX64
  ).forEach { target ->
    target.binaries {
      executable {
        entryPoint = "main"
        baseName = "$baseName-$outputSuffix"
      }
    }
  }

  sourceSets {
    val commonMain by getting
    val jvmMain by getting
    val jsMain by getting
    val linuxX64Main by getting
    val mingwX64Main by getting
    val macosX64Main by getting
  }
}
