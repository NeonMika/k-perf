plugins {
    kotlin("jvm") version "2.3.10"

    `java-gradle-plugin`

    `maven-publish`
}

group = "io.github.neonmika"
version = "0.2.1"

repositories {
    maven {
        url = uri("https://maven.pkg.github.com/dcxp/opentelemetry-kotlin")
        credentials {
            username = project.property("GITHUB_USERNAME") as String
            password = project.property("GITHUB_PASSWORD") as String
        }
    }
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.10")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    implementation(gradleApi())

    implementation(kotlin("stdlib"))
    implementation(kotlin("compiler-embeddable"))

    testImplementation("io.opentelemetry.kotlin.api:all:1.0.570")
    testImplementation("io.opentelemetry.kotlin.sdk:sdk-trace:1.0.570")
    testImplementation("io.github.neonmika:k-perf-otlp-exporter-proto:0.2.1")
    testImplementation("io.github.neonmika:k-perf-otel-proto-utilities:0.2.1")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("dev.zacsweers.kctfork:core:0.12.1")
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("k-perf-otel-proto-timesource") {
            id = "io.github.neonmika.k-perf-otel-proto-timesource-plugin"
            implementationClass = "at.jku.ssw.kperfotel.proto.timesource.gradle.Plugin"
        }
    }
}
