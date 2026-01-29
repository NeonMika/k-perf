plugins {
  kotlin("jvm") version "2.0.20"
  `java-library`
  `maven-publish`
}

group = "at.jku.ssw"
version = "0.1.0"

repositories {
  mavenCentral()
}

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

tasks.register<Jar>("sourcesJar") {
  description = "Generates the sources JAR for this project."
  group = JavaBasePlugin.DOCUMENTATION_GROUP
  archiveClassifier.set("sources")
  from(sourceSets.main.get().allSource)
}

publishing {
  publications {
    create<MavenPublication>("KIRHelperKit") { // this name defines how the Gradle publish commands are named (in this case publishKIRHelperKitPublicationToMavenLocal). Since we can simply publish by calling "publish" / "publishToMavenLocal", this name is not extremely relevant.
      from(components["java"])
      groupId = project.group.toString()
      artifactId = "KIRHelperKit"
      version = project.version.toString() // import using implementation("<groupId>:<artifactId>:<version>")
      artifact(tasks.named("sourcesJar").get())
    }
  }
  repositories {
    mavenLocal()
  }
}