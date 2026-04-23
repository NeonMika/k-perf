@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

import at.jku.ssw.shared.IoaKind

plugins {
  kotlin("multiplatform") version "2.3.0"
  id("io.github.neonmika.instrumentation-overhead-analyzer") version "0.2.1" // dependency on the instrumentation-overhead-analyzer plugin
}

group = "io.github.neonmika"
version = "0.2.1"

repositories {
  mavenCentral()
  mavenLocal() // Add this line to include mavenLocal()
}

val ioaKind = providers.gradleProperty("ioaKind")
    .map { IoaKind.valueOf(it) }
    .getOrElse(IoaKind.None)

val outputSuffix = "kind-${ioaKind.name.lowercase()}"

instrumentationOverheadAnalyzer {
  enabled = true
  kind = ioaKind
}

kotlin {
  jvm {
    compilations.all { }

    mainRun {
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
        archiveClassifier.set(outputSuffix)
        doLast {
          copy {
            from("build/libs")
            from(runtimeDependencyFiles.files)
            into("build/lib")
          }
        }
        doLast {
          copy {
            from("build/lib")
            into(layout.projectDirectory.dir("dist"))
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
        baseName = "$baseName-$outputSuffix"
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
        // To be able to create files in the compiler plugin  (not needed because ioa adds this automatically)
        // implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.5.3")
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

// Copy JS bundle to dist/ after production compile
tasks.configureEach {
  if (name == "jsNodeProductionExecutableCompileSync" || name == "jsProductionExecutableCompileSync") {
    doLast {
      copy {
        from(fileTree("build/js/packages/${project.name}/kotlin") { include("*.js") })
        into(layout.projectDirectory.dir("dist"))
      }
    }
  }
}

// Copy native release binary to dist/ after linking
tasks.configureEach {
  if (name.startsWith("linkReleaseExecutable")) {
    doLast {
      val targetDir = name.removePrefix("linkReleaseExecutable").replaceFirstChar { it.lowercaseChar() }
      copy {
        from("build/bin/$targetDir/releaseExecutable")
        into(layout.projectDirectory.dir("dist"))
      }
    }
  }
}