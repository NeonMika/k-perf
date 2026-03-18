# OpenTelemetry Compiler Plugin

This Compiler Plugin automatically instruments Kotlin (Multiplatform) code to generate and export OpenTelemetry-compliant traces.  
Each function call generates one span while nested function calls preserve the function call hierarchy by propagating span context.

The plugin supports targets including JVM, JavaScript and Native (Linux).

## Usage

Note: The plugin and its dependencies are not published in any maven repository, requiring a local publish and installation using `mavenLocal()`.

Add the following plugin to your `build.gradle.kts`.
```kotlin
id("com.infendro.otel") version "1.0.0"
```

And configure the plugin using the following.
```kotlin
otel {
    // whether the plugin is applied
    // optional, default = true
    enabled = true
    // enables additional terminal output
    // optional, default = false
    debug = true
    // the OpenTelemetry collector host
    // required
    host = "localhost:4318"
    // the OpenTelemetry service (used for identifying the source of spans)
    // required
    service = "plugin"
}
```

All required dependencies are automatically added to the common source set.  
These dependencies include the plugin's utility library (providing essential functions), the [OTLP exporter](https://github.com/FabianSchoenberger/otlp-exporter), the [KMP OpenTelemetry port](https://github.com/dcxp/opentelemetry-kotlin), and Kotlin's coroutines.  
Of these, the utility library and exporter need to be published locally.

## Tests

The tests require the OpenTelemetry Collector to be running and accessible at `localhost:4318`.

Run the tests using the following in the `plugin` module.  
`./gradlew :test --tests "com.infendro.otel.plugin.PluginTest"`
