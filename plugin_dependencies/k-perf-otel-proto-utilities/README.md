# OTel protobuf utilities

Standalone platform helpers for the protobuf/gRPC OTel plugins and `at.jku.ssw.kperfotel.otlp.proto.OtlpProtoExporter`.

The project publishes `io.github.neonmika:k-perf-otel-proto-utilities:0.2.1` and provides:

- `await(exporter)` to wait for pending exports and report export statistics.
- `await(exporter, start)` to additionally report flush duration.
- `env(name)` to read environment variables on JVM, JavaScript, and Native.

Publish with:

```powershell
.\gradlew publishToMavenLocal
```
