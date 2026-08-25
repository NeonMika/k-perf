# OTLP Protobuf Exporter

This is an exporter for dcxp's [KMP OpenTelemetry port](https://github.com/dcxp/opentelemetry-kotlin).  
It exports spans asynchronously to an OpenTelemetry Collector using protobuf over gRPC.

The public exporter class is `com.infendro.otlp.proto.OtlpProtoExporter`.

Supported targets include JVM, JavaScript, and Native (Kotlin).

## Usage

Note: The library is not published in any maven repository, requiring a local publish and installation using mavenLocal().

Add the following to the dependencies in your `build.gradle.kts`.
```kotlin
implementation("com.infendro.otel:otlp-exporter-proto:1.0.1")
// the OpenTelemetry port
implementation("io.opentelemetry.kotlin.api:all:1.0.570")
implementation("io.opentelemetry.kotlin.sdk:sdk-trace:1.0.570")
// to handle asynchronous exports
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
```

See [the prototype](https://github.com/FabianSchoenberger/otel-prototype) for an example.
