# OTel utilities

This Gradle multi-project contains the platform-specific helper functions injected by every compiler plugin under `plugins/otel-*`:

- `await(exporter)` waits for pending exports and reports export statistics.
- `await(exporter, start)` additionally reports flush duration.
- `env(name)` reads environment variables on JVM, JavaScript, and Native.

The Kotlin implementation is shared from `src/`, but it is published as two modules:

- `com.infendro.otel:util:1.0.1` compiles against `otlp-exporter` (JSON/HTTP).
- `com.infendro.otel:util-proto:1.0.1` compiles against `otlp-exporter-proto` (protobuf/gRPC).

Both exporters intentionally expose `com.infendro.otlp.OtlpExporter`, but they are alternative implementations and must not be placed on the same consumer classpath. Separate utility publications preserve the correct transitive exporter dependency while eliminating the previously duplicated utility sources.

Publish both variants with:

```powershell
.\gradlew publishToMavenLocal
```

Publish one variant with `:util:publishToMavenLocal` or `:util-proto:publishToMavenLocal`.
