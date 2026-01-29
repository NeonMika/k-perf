# k-perf
Kotlin backend compiler plugin to generate performance execution traces, including example applications and trace analyzers

## Plugin

The compiler plugin project is located at `plugin/`.
Open the project in IntelliJ for development and refresh the Gradle cache.
The project contains unit tests that can be used for testing the plugin in a JVM environment.
To build and locally publish the plugin run `publishToMavenLocal`.

## Analyzers

At `analyzers/` we host execution trace analyzers.

### Call Graph Visualizer

At `analyzers/call_graph_visualizer` you can find a Python script to generate a DOT / Graphviz graph for a given trace file.

## Kotlin Multiproject (KMP) Examples

At `kmp-examples/` we host example Kotlin projects that can be compiled to different targets.

### CLI Game Of Life

This example is located at `kmp-examples/game_of_life-plain` (without tracing) and `kmp-examples/game_of_life-tracing` (with tracing enabled).
Open the project in IntelliJ for development and refresh the Gradle cache.
Gradle run tasks encompass `jvmRun`, `jsRun` and `runReleaseExecutableMingwX64` (and other native targets).

*How does `kmp-examples/game_of_life-tracing` differ from `kmp-examples/game_of_life-plain`?*
In `build.gradle.kts`, section `plugins`, the "tracing version" has a dependency on the `at.jku.ssw.k-perf-measure-plugin` compiler plugin.
Further, it has a dependency to `kotlinx-io` so that the plugin can write to file.
If you want to use this version, you first have to publish the compiler plugin to local Maven, see section _Plugin_.

## License

The projects in this repository are licensed under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/).