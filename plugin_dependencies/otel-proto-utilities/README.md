# OTel protobuf utilities

Standalone platform helpers for the protobuf/gRPC OTel plugins and `com.infendro.otlp.proto.OtlpProtoExporter`.

The project publishes `com.infendro.otel:otel-proto-utilities:1.0.1` and provides:

- `await(exporter)` to wait for pending exports and report export statistics.
- `await(exporter, start)` to additionally report flush duration.
- `env(name)` to read environment variables on JVM, JavaScript, and Native.

Publish with:

```powershell
.\gradlew publishToMavenLocal
```
