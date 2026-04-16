#!/usr/bin/env bash
# Builds all supported KMP targets for the project in the current working directory.
# Called per-project by build-all-on-ubuntu-separated.yml for discrete CI step visibility.
#
# Task discovery logic mirrors buildAll.sh — keep both in sync when changing target lists.
set -euo pipefail

./gradlew clean

TASKS=$(./gradlew -q tasks --all)

first_task() {
  for candidate in "$@"; do
    if echo "$TASKS" | grep -E -q "^[[:space:]]*${candidate}\b|^:${candidate}\b"; then
      echo "$candidate"
      return 0
    fi
  done
}

run_if_found() {
  local task
  task=$(first_task "$@") || true
  if [ -n "$task" ]; then
    ./gradlew "$task"
  else
    echo "Skipping: no matching task found among: $*"
  fi
}

run_if_found jvmJar compileKotlinJvm
run_if_found jsProductionExecutableCompileSync jsProductionExecutableCompile jsNodeProductionExecutableCompileSync jsNodeProductionExecutableCompile jsBrowserProductionWebpack compileKotlinJs
run_if_found linkReleaseExecutableMingwX64 linkDebugExecutableMingwX64
run_if_found linkReleaseExecutableLinuxX64 linkDebugExecutableLinuxX64
run_if_found linkReleaseExecutableMacosX64 linkDebugExecutableMacosX64 linkReleaseExecutableMacosArm64 linkDebugExecutableMacosArm64
