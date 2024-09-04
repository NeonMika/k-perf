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

This example is located at `kmp-examples/game_of_life`.
Open the project in IntelliJ for development and refresh the Gradle cache.
In `build.gradle.kts`, section `plugins`, one can disable the `at.ssw.k-perf-measure-plugin` compiler plugin by commenting out the respective line.
If you want to apply the plugin, you first have to publish it to local Maven, see section _Plugin_.
Gradle run tasks encompass `jvmRun`, `jsRun` and `runReleaseExecutableMingwX64` (and other native targets).

## License

The projects in this repository are licensed under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/).
