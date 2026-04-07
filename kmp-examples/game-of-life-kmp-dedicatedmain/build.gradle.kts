@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

plugins {
  kotlin("multiplatform") version "2.3.0"
}

group = "io.github.neonmika"
version = "0.2.1"

repositories {
  mavenCentral()
  mavenLocal() // needed in addition to the pluginManagement block in settings.gradle.kts because the plugin in turn depends on another maven project
}

kotlin {
  jvm {
    compilations.all { }

    mainRun {
      mainClass.set("JVMGameOfLifeApplicationKt")
    }
    compilations.all {
      tasks.withType<Jar> {
        doFirst {
          manifest {
            attributes(
              "Main-Class" to "JVMGameOfLifeApplicationKt",
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
  

  /* 
   * https://kotlinlang.org/docs/multiplatform-dsl-reference.html#targets:
   * A target that is not supported by the current host is ignored during building and, therefore, not published.
   */
  // val macosArm64 = macosArm64()
  val macosX64 = macosX64()
  // val linuxArm64 = linuxArm64()
  val linuxX64 = linuxX64()
  val mingwX64 = mingwX64()

  listOf(
    // macosArm64,
    macosX64,
    // linuxArm64,
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
    // Common
    val commonMain by getting {
      dependencies {
        // Because ktor client is using suspend functions
        // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
        // To perform network requests
        // implementation("io.ktor:ktor-client-core:2.3.12")
        // To parse HTML
        // implementation("com.fleeksoft.ksoup:ksoup:0.1.2")
      }
    }
    val commonTest by getting
	
    // JVM
    val jvmMain by getting {
      dependencies {
        // implementation("io.ktor:ktor-client-cio:2.3.12")
      }
    }
    val jvmTest by getting
	
	// Javascript
    val jsMain by getting {
      dependencies {
        // implementation("io.ktor:ktor-client-js:2.3.12")
      }
    }
    val jsTest by getting

	// Windows
    val mingwX64Main by getting {
      dependencies {
        // implementation("io.ktor:ktor-client-winhttp:2.3.12")
      }
    }
    val mingwX64Test by getting
  }
}