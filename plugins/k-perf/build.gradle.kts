// Example: https://github.com/JetBrains/kotlin/tree/master/libraries/examples/kotlin-gradle-subplugin-example

plugins {
  kotlin("jvm") version "2.3.0" // we have a kotlin project

  `java-gradle-plugin` // In this project, we generate a Gradle plugin (which is configured in the gradlePlugin section)

  id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "io.github.neonmika"
version = "0.2.1"

dependencies {
  implementation("io.github.neonmika:KIRHelperKit:0.2.1")
  compileOnly(kotlin("stdlib"))

  // This must be implementation and not compileOnly to have working tests
  implementation(kotlin("compiler-embeddable"))

  // https://youtrack.jetbrains.com/issue/KT-47897/Official-Kotlin-Gradle-plugin-api
  // Use the appropriate version
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0")

  implementation(gradleApi())

  implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.5.3")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testImplementation(platform("org.junit:junit-bom:5.10.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")

  // The following does not support Kotlin 2.0
  // testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.5.0")

  // This one is currently on Kotlin 2.2
  // testImplementation("com.bennyhuo.kotlin:kotlin-compile-testing-extensions:2.0.0-1.3.0")

  // This one supports Kotlin 2.3.0
  testImplementation("dev.zacsweers.kctfork:core:0.12.1")
}

tasks.test {
  useJUnitPlatform()
}

/*
println("[kperf build] rootProject extras:")
rootProject.extra.properties.forEach { (key, value) -> println("[kperf build] -: $key: $value")  }
println("[kperf build] project extras:")
project.extra.properties.forEach { (key, value) -> println("[kperf build] - $key: $value")  }
*/

gradlePlugin {
  plugins {
    create("KPerf") { // this name defines how the Gradle publish commands are named (in this case publishKPerfPluginMarkerMavenPublicationToMavenLocal). Since we can simply publish by calling "publish" / "publishToMavenLocal", this name is not extremely relevant.
      id = "io.github.neonmika.k-perf-plugin" // to use this plugin later in other projects we will use plugins { id("io.github.neonmika.k-perf-plugin") }
      implementationClass = "at.jku.ssw.gradle.KPerfGradlePlugin"
      displayName = "k-perf -- Kotlin Performance Measurement"
      description = "k-perf Gradle Plugin: A Kotlin backend compiler plugin that auto-instruments functions at the IR level to generate execution traces for performance analysis on JVM, JS, and Native targets."
    }
  }
}

mavenPublishing {
  publishToMavenCentral()
  if (providers.gradleProperty("signingInMemoryKey").isPresent ||
      providers.gradleProperty("signing.secretKeyRingFile").isPresent) {
    signAllPublications()
  }

  coordinates(group.toString(), "k-perf", version.toString())

  pom {
    name = "k-perf"
    description = "A Kotlin backend compiler plugin that auto-instruments functions at the IR level to generate execution traces for performance analysis on JVM, JS, and Native targets."
    inceptionYear = "2024"
    url = "https://github.com/NeonMika/k-perf/"
    licenses {
      license {
        name = "GNU Lesser General Public License v3.0"
        url = "https://www.gnu.org/licenses/lgpl-3.0.html"
        distribution = "repo"
      }
    }
    developers {
      developer {
        id = "NeonMika"
        name = "Markus Weninger"
        url = "https://github.com/NeonMika/"
      }
    }
    scm {
      url = "https://github.com/NeonMika/k-perf/"
      connection = "scm:git:git://github.com/NeonMika/k-perf.git"
      developerConnection = "scm:git:ssh://git@github.com/NeonMika/k-perf.git"
    }
  }
}