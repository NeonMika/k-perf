// Example: https://github.com/JetBrains/kotlin/tree/master/libraries/examples/kotlin-gradle-subplugin-example

plugins {
    kotlin("jvm") version "2.0.20" // we have a kotlin project

    `java-gradle-plugin` // we generate a gradle plugin configured in the gradlePlugin section
    `kotlin-dsl` // To be able to use Kotlin when developing the Plugin<Project> class

    `maven-publish` // the generated plugin will be published to mavenLocal

    id("com.gradleup.shadow") version "8.3.6"
}

group = "at.ssw"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("compiler-embeddable"))
    // https://youtrack.jetbrains.com/issue/KT-47897/Official-Kotlin-Gradle-plugin-api
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:2.0.20") // Use the appropriate version
    implementation(gradleApi())
    // must also be in target program!
    // this is here for the tests to run
    implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.5.3")
    implementation("io.ktor:ktor-server-core:3.1.3")
    implementation("io.ktor:ktor-server-netty:3.1.3")
    implementation("io.ktor:ktor-server-content-negotiation:3.1.3")
    implementation("io.ktor:ktor-serialization-gson:3.1.3")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // The following does not support Kotlin 2.0 yet
    // testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.5.0")

    testImplementation("com.bennyhuo.kotlin:kotlin-compile-testing-extensions:2.0.0-1.3.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    isZip64 = true
}

/*
println("rootProject extras:")
rootProject.extra.properties.forEach { (key, value) -> println("-: $key: $value")  }
println("project extras:")
project.extra.properties.forEach { (key, value) -> println("- $key: $value")  }
*/

gradlePlugin {
    plugins {
        create("kIRVisualizer") {
            id = "at.ssw.k-ir-visualizer-plugin" // to use this plugin later in other projects we will use plugins { id("at.ssw.k-ir-visualize") }
            implementationClass = "at.ssw.gradle.KIRVisualizerGradlePlugin"
        }
    }
}
