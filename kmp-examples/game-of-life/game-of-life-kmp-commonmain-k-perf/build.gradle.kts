@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

plugins {
  kotlin("multiplatform") version "2.3.10"
  id("io.github.neonmika.k-perf-plugin") version "0.2.1" // dependency on the k-perf-plugin plugin
}

group = "io.github.neonmika"
version = "0.2.1"

repositories {
  mavenCentral()
  mavenLocal() // Add this line to include mavenLocal()
}

val kperfEnabled = providers.gradleProperty("kperfEnabled")
  .map { it.toBoolean() }
  .also { if(it.isPresent) println("kperfEnabled specified, set to ${it.get()}") else println("kperfEnabled not specified, set to default true") }
  .getOrElse(true)

val kperfFlushEarly = providers.gradleProperty("kperfFlushEarly")
  .map { it.toBoolean() }
  .also { if(it.isPresent) println("kperfFlushEarly specified, set to ${it.get()}") else println("kperfFlushEarly not specified, set to default false") }
  .getOrElse(false)

val kperfInstrumentPropertyAccessors = providers.gradleProperty("kperfInstrumentPropertyAccessors")
  .map { it.toBoolean() }
  .also { if(it.isPresent) println("kperfInstrumentPropertyAccessors specified, set to ${it.get()}") else println("kperfInstrumentPropertyAccessors not specified, set to default false") }
  .getOrElse(false)

val kperfTestKIR = providers.gradleProperty("kperfTestKIR")
  .map { it.toBoolean() }
  .also { if(it.isPresent) println("kperfTestKIR specified, set to ${it.get()}") else println("kperfTestKIR not specified, set to default false") }
  .getOrElse(false)

val kperfMethods = providers.gradleProperty("kperfMethods")
  .also { if(it.isPresent) println("kperfMethods specified, set to ${it.get()}") else println("kperfMethods not specified, set to default .*") }
  .getOrElse(".*")

kperf {
  enabled = kperfEnabled
  flushEarly = kperfFlushEarly
  instrumentPropertyAccessors = kperfInstrumentPropertyAccessors
  testKIR = kperfTestKIR
  methods = kperfMethods
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
        // To be able to create files in the compiler plugin (not needed because k-perf adds this automatically)
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
        from("build/js/packages/${project.name}/kotlin/${project.name}.js")
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