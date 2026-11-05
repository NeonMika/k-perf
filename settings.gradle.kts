rootProject.name = "k-perf-masterproject"

includeBuild("KIRHelperKit") { name = "KIRHelperKit" }

includeBuild("plugin_dependencies/k-perf-otlp-exporter") { name = "k-perf-otlp-exporter" }
includeBuild("plugin_dependencies/k-perf-otlp-exporter-proto") { name = "k-perf-otlp-exporter-proto" }
includeBuild("plugin_dependencies/k-perf-otel-utilities") { name = "k-perf-otel-utilities" }
includeBuild("plugin_dependencies/k-perf-otel-proto-utilities") { name = "k-perf-otel-proto-utilities" }

includeBuild("plugins/k-perf") { name = "k-perf-plugin" }
includeBuild("plugins/instrumentation-overhead-analyzer") { name = "ioa-plugin" }
includeBuild("plugins/k-perf-otel/plugin") { name = "k-perf-otel" }
includeBuild("plugins/k-perf-otel-experiments-ssp-2026/k-perf-otel-proto/plugin") { name = "k-perf-otel-proto" }
includeBuild("plugins/k-perf-otel-experiments-ssp-2026/k-perf-otel-proto-timesource/plugin") { name = "k-perf-otel-proto-timesource" }
includeBuild("plugins/k-perf-otel-experiments-ssp-2026/k-perf-otel-proto-anchored/plugin") { name = "k-perf-otel-proto-anchored" }
includeBuild("plugins/k-perf-otel-experiments-ssp-2026/k-perf-otel-proto-sampler/plugin") { name = "k-perf-otel-proto-sampler" }
includeBuild("plugins/k-perf-otel-experiments-ssp-2026/k-perf-otel-proto-fastbatch/plugin") { name = "k-perf-otel-proto-fastbatch" }
includeBuild("plugins/k-perf-otel-experiments-ssp-2026/k-perf-otel-proto-combined/plugin") { name = "k-perf-otel-proto-combined" }

includeBuild("kmp-examples/game-of-life/game-of-life-kmp-commonmain-baseline") { name = "game-of-life-kmp-commonmain-baseline" }
includeBuild("kmp-examples/game-of-life/game-of-life-kmp-commonmain-ioa") { name = "game-of-life-kmp-commonmain-ioa" }
includeBuild("kmp-examples/game-of-life/game-of-life-kmp-commonmain-k-perf") { name = "game-of-life-kmp-commonmain-k-perf" }
includeBuild("kmp-examples/game-of-life/game-of-life-kmp-dedicatedmain-baseline") { name = "game-of-life-kmp-dedicatedmain-baseline" }
includeBuild("kmp-examples/game-of-life/game-of-life-kmp-dedicatedmain-k-perf") { name = "game-of-life-kmp-dedicatedmain-k-perf" }

includeBuild("kmp-examples/fibonacci/fibonacci-baseline") { name = "fibonacci-baseline" }
includeBuild("kmp-examples/fibonacci/fibonacci-k-perf") { name = "fibonacci-k-perf" }
includeBuild("kmp-examples/fibonacci/fibonacci-k-perf-otel") { name = "fibonacci-k-perf-otel" }
includeBuild("kmp-examples/fibonacci/k-perf-otel-experiments-ssp-2026/fibonacci-k-perf-otel-proto") { name = "fibonacci-k-perf-otel-proto" }
includeBuild("kmp-examples/fibonacci/k-perf-otel-experiments-ssp-2026/fibonacci-k-perf-otel-proto-timesource") { name = "fibonacci-k-perf-otel-proto-timesource" }
includeBuild("kmp-examples/fibonacci/k-perf-otel-experiments-ssp-2026/fibonacci-k-perf-otel-proto-anchored") { name = "fibonacci-k-perf-otel-proto-anchored" }
includeBuild("kmp-examples/fibonacci/k-perf-otel-experiments-ssp-2026/fibonacci-k-perf-otel-proto-sampler") { name = "fibonacci-k-perf-otel-proto-sampler" }
includeBuild("kmp-examples/fibonacci/k-perf-otel-experiments-ssp-2026/fibonacci-k-perf-otel-proto-fastbatch") { name = "fibonacci-k-perf-otel-proto-fastbatch" }
includeBuild("kmp-examples/fibonacci/k-perf-otel-experiments-ssp-2026/fibonacci-k-perf-otel-proto-combined") { name = "fibonacci-k-perf-otel-proto-combined" }
