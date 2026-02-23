@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

import at.jku.ssw.shared.IoaKind

plugins {
  kotlin("multiplatform") version "2.3.0"
  id("at.jku.ssw.instrumentation-overhead-analyzer") version "0.1.0" // dependency on the instrumentation-overhead-analyzer plugin
}

group = "at.jku.ssw"
version = "0.1.0"

repositories {
  mavenCentral()
  mavenLocal() // Add this line to include mavenLocal()
}

val ioaKind = providers.gradleProperty("ioaKind")
    .map { IoaKind.valueOf(it) }
    .getOrElse(IoaKind.None)

instrumentationOverheadAnalyzer {
  enabled = true
  kind = ioaKind
}

kotlin {
  jvm {
    compilations.all { }
    /*
    testRuns["test"].executionTask.configure {
        useJUnitPlatform()
    }
    */
    mainRun {
      // Define the main class to execute
      mainClass.set("CommonGameOfLifeApplicationKt")
    }
    compilations.all {
      tasks.withType<Jar> {
        doFirst {
          manifest {
            attributes(
              "Main-Class" to "CommonGameOfLifeApplicationKt",
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

  // val hostOs = System.getProperty("os.name")
  // val isArm64 = System.getProperty("os.arch") == "aarch64"
  // val isMingwX64 = hostOs.startsWith("Windows")

  /* https://kotlinlang.org/docs/multiplatform-dsl-reference.html#targets:
  A target that is not supported by the current host is ignored during building and, therefore, not published.
   */
  // val macosArm64 = macosArm64()
  val macosX64 = macosX64()
  // val linuxArm64 = linuxArm64()
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
      }
    }
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        // Because ktor client is using suspend functions
        // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
        // To perform network requests
        // implementation("io.ktor:ktor-client-core:2.3.12")
        // To parse HTML
        // implementation("com.fleeksoft.ksoup:ksoup:0.1.2")
        // To be able to create files in the compiler plugin (not needed because k-perf adds this automatically)
        // implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.5.3")
      }
    }
    val commonTest by getting {
      dependencies {
        //implementation(kotlin("test"))
      }
    }
    val jvmMain by getting {
      dependencies {
        // implementation("io.ktor:ktor-client-cio:2.3.12")
      }
    }
    val jvmTest by getting
    val jsMain by getting {
      dependencies {
        // implementation("io.ktor:ktor-client-js:2.3.12")
      }
    }
    val jsTest by getting

    val mingwX64Main by getting {
      dependencies {
        // implementation("io.ktor:ktor-client-winhttp:2.3.12")
      }
    }
    val mingwX64Test by getting
  }
}

/*
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        verbose = true
    }
}
*/