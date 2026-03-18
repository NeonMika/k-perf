plugins {
    kotlin("jvm") version "2.1.10"

    `java-gradle-plugin`
    `kotlin-dsl`

    `maven-publish`
}

group = "com.infendro.otel"
version = "1.0.0"

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
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    implementation(gradleApi())

    implementation(kotlin("stdlib"))
    implementation(kotlin("compiler-embeddable"))

    testImplementation("io.opentelemetry.kotlin.api:all:1.0.570")
    testImplementation("io.opentelemetry.kotlin.sdk:sdk-trace:1.0.570")
    testImplementation("com.infendro.otel:otlp-exporter:1.0.0")
    testImplementation("com.infendro.otel:util:1.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("com.bennyhuo.kotlin:kotlin-compile-testing-extensions:2.1.0-1.3.0")
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("otel") {
            id = "com.infendro.otel"
            implementationClass = "com.infendro.otel.gradle.Plugin"
        }
    }
}
