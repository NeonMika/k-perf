rootProject.name = "k-perf-masterproject"

includeBuild("KIRHelperKit") { name = "KIRHelperKit" }

includeBuild("otlp-exporter") { name = "otlp-exporter" }
includeBuild("otlp-exporter-proto") { name = "otlp-exporter-proto" }
includeBuild("plugins/otel-plugin/util") { name = "otel-util" }
includeBuild("plugins/otel-plugin-proto/util") { name = "otel-util-proto" }

includeBuild("plugins/k-perf") { name = "k-perf-plugin" }
includeBuild("plugins/instrumentation-overhead-analyzer") { name = "ioa-plugin" }
includeBuild("plugins/otel-plugin/plugin") { name = "otel-plugin" }
includeBuild("plugins/otel-plugin-proto/plugin") { name = "otel-plugin-proto" }
includeBuild("plugins/otel-plugin-proto-timesource/plugin") { name = "otel-plugin-proto-timesource" }
includeBuild("plugins/otel-plugin-proto-anchored/plugin") { name = "otel-plugin-proto-anchored" }
includeBuild("plugins/otel-plugin-proto-sampler/plugin") { name = "otel-plugin-proto-sampler" }
includeBuild("plugins/otel-plugin-proto-fastbatch/plugin") { name = "otel-plugin-proto-fastbatch" }
includeBuild("plugins/otel-plugin-proto-combined/plugin") { name = "otel-plugin-proto-combined" }

includeBuild("kmp-examples/game-of-life-kmp-commonmain") { name = "game-of-life-kmp-commonmain" }
includeBuild("kmp-examples/game-of-life-kmp-commonmain-ioa") { name = "game-of-life-kmp-commonmain-ioa" }
includeBuild("kmp-examples/game-of-life-kmp-commonmain-k-perf") { name = "game-of-life-kmp-commonmain-k-perf" }
includeBuild("kmp-examples/game-of-life-kmp-dedicatedmain") { name = "game-of-life-kmp-dedicatedmain" }
includeBuild("kmp-examples/game-of-life-kmp-dedicatedmain-k-perf") { name = "game-of-life-kmp-dedicatedmain-k-perf" }

includeBuild("kmp-examples/comparison-baseline") { name = "comparison-baseline" }
includeBuild("kmp-examples/comparison-k-perf") { name = "comparison-k-perf" }
includeBuild("kmp-examples/comparison-otel") { name = "comparison-otel" }
includeBuild("kmp-examples/comparison-otel-proto") { name = "comparison-otel-proto" }
includeBuild("kmp-examples/comparison-otel-proto-timesource") { name = "comparison-otel-proto-timesource" }
includeBuild("kmp-examples/comparison-otel-proto-anchored") { name = "comparison-otel-proto-anchored" }
includeBuild("kmp-examples/comparison-otel-proto-sampler") { name = "comparison-otel-proto-sampler" }
includeBuild("kmp-examples/comparison-otel-proto-fastbatch") { name = "comparison-otel-proto-fastbatch" }
includeBuild("kmp-examples/comparison-otel-proto-combined") { name = "comparison-otel-proto-combined" }
