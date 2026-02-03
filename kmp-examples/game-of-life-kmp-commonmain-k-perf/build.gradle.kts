plugins {
  kotlin("multiplatform") version "2.0.20"
  id("at.jku.ssw.k-perf-plugin") version "0.0.3" // dependency on the k-perf-measure-plugin plugin
}

group = "at.jku.ssw"
version = "0.0.3"

repositories {
  mavenCentral()
  mavenLocal() // Add this line to include mavenLocal()
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


val flushEarlyTag = if (kperfFlushEarly) "true" else "false"
val outputSuffix = "flushEarly-$flushEarlyTag"

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

  js(IR) {
    moduleName = "${project.name}-$outputSuffix"
    nodejs {
      passProcessArgvToMainFunction()
    }
    binaries.executable()
  }

  val hostOs = System.getProperty("os.name")
  val isArm64 = System.getProperty("os.arch") == "aarch64"
  val isMingwX64 = hostOs.startsWith("Windows")

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
        baseName = "$baseName-$outputSuffix"
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