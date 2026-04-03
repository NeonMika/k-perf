plugins {
  kotlin("jvm") version "2.3.0"
  `java-library`
  id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "io.github.neonmika"
version = "0.2.0"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}

dependencies {
  testImplementation(kotlin("test"))
  compileOnly(kotlin("compiler-embeddable"))
  implementation(kotlin("stdlib"))
  compileOnly("org.jetbrains.kotlinx:kotlinx-io-core:0.5.3")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile> {
  compilerOptions {
    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
  }
}

mavenPublishing {
  publishToMavenCentral()
  if (providers.gradleProperty("signingInMemoryKey").isPresent ||
      providers.gradleProperty("signing.secretKeyRingFile").isPresent) {
    signAllPublications()
  }

  coordinates(group.toString(), "KIRHelperKit", version.toString())

  pom {
    name = "KIRHelperKit"
    description = "A utility library that simplifies Kotlin compiler plugin development by providing IR element finding, a call DSL, and abstracted file I/O utilities."
    inceptionYear = "2025"
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
        name = "Dr. Markus Weninger"
        url = "https://github.com/NeonMika/"
      }
      developer {
        id = "LorenzBader"
        name = "Lorenz Bader"
        url = "https://github.com/LorenzBader/"
      }
    }
    scm {
      url = "https://github.com/NeonMika/k-perf/"
      connection = "scm:git:git://github.com/NeonMika/k-perf.git"
      developerConnection = "scm:git:ssh://git@github.com/NeonMika/k-perf.git"
    }
  }
}