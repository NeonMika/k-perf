plugins {
    kotlin("multiplatform") version "2.3.10"
    id("io.github.timortel.kmpgrpc.plugin") version "2.0.0"
    id("maven-publish")
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
    mavenCentral()
}

kotlin {
    applyDefaultHierarchyTemplate()

    jvm()
    js {
        nodejs()
        useCommonJs()
    }
    linuxX64()
    mingwX64()

    sourceSets {
        commonMain.dependencies {
            implementation("io.opentelemetry.kotlin.api:all:1.0.570")
            implementation("io.opentelemetry.kotlin.sdk:sdk-trace:1.0.570")

            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("io.github.timortel:kmp-grpc-core:2.0.0")
        }

        val nativeMain by getting {
            // Link the kmpgrpc-generated native source code (linuxX64)
            kotlin.srcDir("${layout.buildDirectory.get()}/generated/source/kmp-grpc/nativeMain/kotlin")
        }
    }
}

kmpGrpc {
    common()
    jvm()
    js()
    native()
    protoSourceFolders = project.files("src/main/proto")
}

// kmpgrpc 2.0.0 declares `generatedSourcesOutputFolder` and `wellKnownTypesFolder`
// as @InputDirectory on GenerateKmpGrpcSourcesTask. After `gradle clean` these
// dirs don't exist yet and Gradle 8.10's strict input validation refuses to run
// the task. Pre-create them so clean builds (used by the benchmark script) work.
val ensureKmpGrpcDirs = tasks.register("ensureKmpGrpcDirs") {
    doLast {
        val base = layout.buildDirectory.get().asFile
        base.resolve("generated/source/kmp-grpc").mkdirs()
        base.resolve("well-known-protos").mkdirs()
    }
}
afterEvaluate {
    tasks.matching { it.name == "generateKmpGrpcSources" }.configureEach {
        dependsOn(ensureKmpGrpcDirs)
    }
}
