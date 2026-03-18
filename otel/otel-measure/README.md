# OpenTelemetry measurements

This project measures execution times for:
* no tracing
* manual tracing
* tracing using our OpenTelemetry plugin

## Running

Run the following in the `reference`, `manual`, and `plugin` module. \
`./gradlew release` \
This task builds the application for all targets (JVM, JavaScript, and Linux).

In `./measure/measure.sh`, comment in the relevant variables to execute and measure the application. \
To process the measurements for statistics, run `process.py`.
