# OTel utilities

Standalone platform helpers for the JSON/HTTP OTel plugin and `at.jku.ssw.kperfotel.otlp.json.OtlpJsonExporter`.

The project publishes `io.github.neonmika:k-perf-otel-utilities:0.2.1` and provides:

- `await(exporter)` to wait for pending exports and report export statistics.
- `await(exporter, start)` to additionally report flush duration.
- `env(name)` to read environment variables on JVM, JavaScript, and Native.

Publish with:

```powershell
.\gradlew publishToMavenLocal
```
