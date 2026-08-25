# OTel utilities

This Gradle multi-project contains the platform-specific helper functions injected by every compiler plugin under `plugins/otel-*`:

- `await(exporter)` waits for pending exports and reports export statistics.
- `await(exporter, start)` additionally reports flush duration.
- `env(name)` reads environment variables on JVM, JavaScript, and Native.

The Kotlin implementation is shared from `src/`, but it is published as two modules:

- `com.infendro.otel:util:1.0.1` compiles against `com.infendro.otlp.json.OtlpJsonExporter` from `otlp-exporter` (JSON/HTTP).
- `com.infendro.otel:util-proto:1.0.1` compiles against `com.infendro.otlp.proto.OtlpProtoExporter` from `otlp-exporter-proto` (protobuf/gRPC).

The exporters use distinct packages and class names, so both dependencies can be inspected without ambiguous or duplicate class identities. Separate utility publications preserve the correct transitive exporter dependency while eliminating the previously duplicated utility implementation.

Publish both variants with:

```powershell
.\gradlew publishToMavenLocal
```

Publish one variant with `:util:publishToMavenLocal` or `:util-proto:publishToMavenLocal`.
