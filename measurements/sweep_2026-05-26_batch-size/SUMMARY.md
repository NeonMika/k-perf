# Batch-size sweep - 2026_05_26_16_32_27

Parameters: WarmupCount=0, RunCount=20, StepCount=150

## Configurations

| Label | queueSize | maxExportBatchSize |
|---|---:|---:|
| default | 2048 | Int.MAX_VALUE |
| small-batch | 512 | 64 |
| matched-medium | 2048 | 2048 |
| big-buffer | 8192 | 8192 |
| pre-fix-replica | Int.MAX_VALUE | 512 |

Reference: SimpleSpanProcessor was previously evaluated in
[../comparison_2026-05-26_ssp-smoke/results.md](../comparison_2026-05-26_ssp-smoke/results.md);
it is not included here because the multi-hour drain at high StepCount makes
a RunCount=20, StepCount=150 measurement impractical, and the smoke run
already established that SSP is dominated by every BSP configuration on
per-method cost (~11x slower on JVM, ~2.2x on JS in steady state).

## Overhead per instrumented method (ns)

Each cell: `steady ns/method (envelope P10 ns/method)`. Lower is better.
GeoMean is across the available platforms.

Note: Native (mingwX64) measurements are absent because Smart App Control on
the sweep machine blocks llvmstubs.dll on every fresh extraction triggered by
the per-config Gradle-property change. JVM + JS evidence is sufficient for
picking a batching-config winner.

### otel-proto

| Config | JVM | JS | Native | GeoMean steady |
|---|---:|---:|---:|---:|
| default | 2,197 (1,590) | 4,453 (3,726) | N/A | 3,128 |
| small-batch | 15,291 (10,154) | 16,381 (12,505) | N/A | 15,827 |
| matched-medium | 15,715 (1,318) | 45,118 (4,594) | N/A | 26,628 |
| big-buffer | 24,030 (463) | 110,006 (4,709) | N/A | 51,415 |
| pre-fix-replica | 7,928 (1,857) | 20,384 (4,635) | N/A | 12,712 |

### otel-proto-timesource

| Config | JVM | JS | Native | GeoMean steady |
|---|---:|---:|---:|---:|
| default | 2,022 (1,593) | 5,581 (4,513) | N/A | 3,359 |
| small-batch | 13,742 (9,931) | 17,594 (13,178) | N/A | 15,549 |
| matched-medium | 15,599 (1,390) | 45,855 (5,497) | N/A | 26,745 |
| big-buffer | 23,262 (484) | 104,531 (5,574) | N/A | 49,311 |
| pre-fix-replica | 7,561 (1,795) | 20,925 (5,667) | N/A | 12,579 |

## Winner

- **otel-proto**: `default` (lowest geomean of steady ns/method)
- **otel-proto-timesource**: `default` (lowest geomean of steady ns/method)

## Adoption checklist

If both variants agree on the winning config, update four locations:

1. `kmp-examples/comparison-otel-proto/build.gradle.kts` - change `getOrElse(2048)` and `getOrElse(Int.MAX_VALUE)` defaults for `otelProtoMaxQueueSize` and `otelProtoMaxExportBatchSize`.
2. `kmp-examples/comparison-otel-proto-timesource/build.gradle.kts` - same for `otelProtoTsMaxQueueSize` / `otelProtoTsMaxExportBatchSize`.
3. `plugins/otel-plugin-proto/.../Registrar.kt` - adjust `configuration[KEY_MAX_QUEUE_SIZE] ?: 2048` and `configuration[KEY_MAX_EXPORT_BATCH_SIZE] ?: Int.MAX_VALUE` fallbacks.
4. `plugins/otel-plugin-proto-timesource/.../Registrar.kt` - same fallbacks.

If the variants disagree on a winner, inspect per-platform cells and choose based
on which platform's overhead matters more for the deployment target.
