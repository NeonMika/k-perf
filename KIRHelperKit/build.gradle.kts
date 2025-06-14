plugins {
    kotlin("jvm") version "2.0.20"
    `java-library`
    `maven-publish`
    `kotlin-dsl`
}

group = "at.ssw"
version = "0.0.2"

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
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:2.0.20")
    implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.5.3")
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
        create<MavenPublication>("mavenKotlin") {
            from(components["java"])
            groupId = "at.ssw"
            artifact(tasks.named("sourcesJar").get())
            artifactId = "KIRHelperKit"
            version = "0.0.2"
        }
    }
    repositories {
        mavenLocal()
    }
}