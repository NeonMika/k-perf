// Example: https://github.com/JetBrains/kotlin/tree/master/libraries/examples/kotlin-gradle-subplugin-example

plugins {
  kotlin("jvm") version "2.3.0" // we have a kotlin project

  `java-gradle-plugin` // In this project, we generate a Gradle plugin (which is configured in the gradlePlugin section)

  `maven-publish` // the generated plugin will be published to mavenLocal
}

group = "at.jku.ssw"
version = "0.1.0"

dependencies {
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
println("rootProject extras:")
rootProject.extra.properties.forEach { (key, value) -> println("-: $key: $value")  }
println("project extras:")
project.extra.properties.forEach { (key, value) -> println("- $key: $value")  }
*/

gradlePlugin {
  plugins {
    create("InstrumentationOverheadAnalyzer") { // this name defines how the Gradle publish commands are named (in this case publishInstrumentationOverheadAnalyzerPluginMarkerMavenPublicationToMavenLocal). Since we can simply publish by calling "publish" / "publishToMavenLocal", this name is not extremely relevant.
      id =
        "at.jku.ssw.instrumentation-overhead-analyzer" // to use this plugin later in other projects we will use plugins { id("at.jku.ssw.instrumentation-overhead-analyzer") }
      implementationClass = "at.jku.ssw.gradle.InstrumentationOverheadAnalyzerGradlePlugin"
    }
  }
}