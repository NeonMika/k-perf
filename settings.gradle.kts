rootProject.name = "k-perf-masterproject"

includeBuild("KIRHelperKit") { name = "KIRHelperKit" }

includeBuild("plugin_dependencies/otlp-exporter") { name = "otlp-exporter" }
includeBuild("plugin_dependencies/otlp-exporter-proto") { name = "otlp-exporter-proto" }
includeBuild("plugin_dependencies/otel-utilities") { name = "otel-utilities" }

includeBuild("plugins/k-perf") { name = "k-perf-plugin" }
includeBuild("plugins/instrumentation-overhead-analyzer") { name = "ioa-plugin" }
includeBuild("plugins/otel-plugin/plugin") { name = "otel-plugin" }
includeBuild("plugins/otel-plugin-proto/plugin") { name = "otel-plugin-proto" }
includeBuild("plugins/otel-plugin-proto-timesource/plugin") { name = "otel-plugin-proto-timesource" }
includeBuild("plugins/otel-plugin-proto-anchored/plugin") { name = "otel-plugin-proto-anchored" }
includeBuild("plugins/otel-plugin-proto-sampler/plugin") { name = "otel-plugin-proto-sampler" }
includeBuild("plugins/otel-plugin-proto-fastbatch/plugin") { name = "otel-plugin-proto-fastbatch" }
includeBuild("plugins/otel-plugin-proto-combined/plugin") { name = "otel-plugin-proto-combined" }

includeBuild("kmp-examples/game-of-life/game-of-life-kmp-commonmain-baseline") { name = "game-of-life-kmp-commonmain-baseline" }
includeBuild("kmp-examples/game-of-life/game-of-life-kmp-commonmain-ioa") { name = "game-of-life-kmp-commonmain-ioa" }
includeBuild("kmp-examples/game-of-life/game-of-life-kmp-commonmain-k-perf") { name = "game-of-life-kmp-commonmain-k-perf" }
includeBuild("kmp-examples/game-of-life/game-of-life-kmp-dedicatedmain-baseline") { name = "game-of-life-kmp-dedicatedmain-baseline" }
includeBuild("kmp-examples/game-of-life/game-of-life-kmp-dedicatedmain-k-perf") { name = "game-of-life-kmp-dedicatedmain-k-perf" }

includeBuild("kmp-examples/fibonacci/fibonacci-baseline") { name = "fibonacci-baseline" }
includeBuild("kmp-examples/fibonacci/fibonacci-k-perf") { name = "fibonacci-k-perf" }
includeBuild("kmp-examples/fibonacci/fibonacci-otel") { name = "fibonacci-otel" }
includeBuild("kmp-examples/fibonacci/fibonacci-otel-proto") { name = "fibonacci-otel-proto" }
includeBuild("kmp-examples/fibonacci/fibonacci-otel-proto-timesource") { name = "fibonacci-otel-proto-timesource" }
includeBuild("kmp-examples/fibonacci/fibonacci-otel-proto-anchored") { name = "fibonacci-otel-proto-anchored" }
includeBuild("kmp-examples/fibonacci/fibonacci-otel-proto-sampler") { name = "fibonacci-otel-proto-sampler" }
includeBuild("kmp-examples/fibonacci/fibonacci-otel-proto-fastbatch") { name = "fibonacci-otel-proto-fastbatch" }
includeBuild("kmp-examples/fibonacci/fibonacci-otel-proto-combined") { name = "fibonacci-otel-proto-combined" }
