# OpenTelemetry Prototype

This is a prototype for using OpenTelemetry tracing in Kotlin Multiplatform.

Supported targets include JVM, JavaScript, and Native (Kotlin).

## Running

1. Start the OpenTelemetry Collector and Jaeger Backend.  
   `docker compose -f ./otel/compose.yaml up`

2. Run the application.
   * JVM - `./gradlew jvmRun`
   * JavaScript - `./gradlew jsNodeRun`
   * Linux - `./gradlew runReleaseExecutableLinuxX64`

3. Access Jaeger at `http://localhost:16686`
