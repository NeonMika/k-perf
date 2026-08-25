# OTLP Exporter

This is an exporter for dcxp's [KMP OpenTelemetry port](https://github.com/dcxp/opentelemetry-kotlin).  
It exports spans asynchronously (using Ktor) to an OpenTelemetry Collector using HTTP and JSON.

Supported targets include JVM, JavaScript, and Native (Kotlin).

## Usage

Note: The library is not published in any maven repository, requiring a local publish and installation using mavenLocal().

Add the following to the dependencies in your `build.gradle.kts`.
```kotlin
implementation("com.infendro.otel:otlp-exporter:1.0.0")
// the OpenTelemetry port
implementation("io.opentelemetry.kotlin.api:all:1.0.570")
implementation("io.opentelemetry.kotlin.sdk:sdk-trace:1.0.570")
// to handle the asynchronous HTTP requests
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
```

See [the prototype](https://github.com/FabianSchoenberger/otel-prototype) for an example.
