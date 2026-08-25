@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

plugins {
  kotlin("multiplatform") version "2.3.10"
}

group = "at.jku.ssw"
version = "0.1.0"

repositories {
  mavenCentral()
  mavenLocal()
}

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
    nodejs {
      passProcessArgvToMainFunction()
    }
    binaries.executable()
  }

  val linuxX64 = linuxX64()
  val mingwX64 = mingwX64()

  listOf(
    linuxX64,
    mingwX64
  ).forEach { target ->
    target.binaries {
      executable {
        entryPoint = "main"
      }
    }
  }

  sourceSets {
    val commonMain by getting
    val jvmMain by getting
    val jsMain by getting
    val linuxX64Main by getting
    val mingwX64Main by getting
  }
}
