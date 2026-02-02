#!/usr/bin/env bash
set -euo pipefail

CLEAN_BUILD=true

for arg in "$@"; do
  case "$arg" in
    --no-clean)
      CLEAN_BUILD=false
      ;;
  esac
done

run_gradle() {
  local title="$1"
  local path="$2"
  shift 2
  local tasks=("$@")

  echo ""
  echo "=========================================="
  echo "$title"
  echo "Path: $path"
  echo "Tasks: ${tasks[*]}"
  echo "=========================================="

  pushd "$path" > /dev/null
  if [ "$CLEAN_BUILD" = true ]; then
    ./gradlew clean "${tasks[@]}"
  else
    ./gradlew "${tasks[@]}"
  fi
  popd > /dev/null

  echo "$title completed successfully."
}

first_task() {
  local task_list="$1"
  shift
  local candidate
  for candidate in "$@"; do
    if echo "$task_list" | grep -E -q "^[[:space:]]*$candidate\b|^:$candidate\b"; then
      echo "$candidate"
      return 0
    fi
  done
  return 1
}

run_gradle_task_if_present() {
  local title="$1"
  local task_name="$2"

  if [ -z "$task_name" ]; then
    echo "Skipping $title (task not found)"
    return 0
  fi

  echo ""
  echo "=========================================="
  echo "$title"
  echo "Task: $task_name"
  echo "=========================================="

  ./gradlew "$task_name"
  echo "$title completed successfully."
}

run_kmp_build() {
  local title="$1"
  local path="$2"

  echo ""
  echo "=========================================="
  echo "$title (Kotlin Multiplatform)"
  echo "Path: $path"
  echo "=========================================="

  pushd "$path" > /dev/null
  if [ "$CLEAN_BUILD" = true ]; then
    ./gradlew clean
  fi

  local task_list
  task_list=$(./gradlew -q tasks --all)

  local jvm_task
  jvm_task=$(first_task "$task_list" jvmJar compileKotlinJvm || true)
  local js_task
  js_task=$(first_task "$task_list" jsProductionExecutableCompileSync jsProductionExecutableCompile jsNodeProductionExecutableCompileSync jsNodeProductionExecutableCompile jsBrowserProductionWebpack compileKotlinJs || true)
  local windows_task
  windows_task=$(first_task "$task_list" linkReleaseExecutableMingwX64 linkDebugExecutableMingwX64 || true)
  local linux_task
  linux_task=$(first_task "$task_list" linkReleaseExecutableLinuxX64 linkDebugExecutableLinuxX64 || true)
  local mac_task
  mac_task=$(first_task "$task_list" linkReleaseExecutableMacosX64 linkDebugExecutableMacosX64 linkReleaseExecutableMacosArm64 linkDebugExecutableMacosArm64 || true)

  run_gradle_task_if_present "$title - JVM build" "$jvm_task"
  run_gradle_task_if_present "$title - JS build" "$js_task"
  run_gradle_task_if_present "$title - Windows build" "$windows_task"
  run_gradle_task_if_present "$title - Linux build" "$linux_task"
  run_gradle_task_if_present "$title - Mac build" "$mac_task"

  popd > /dev/null

  echo "$title completed successfully."
}

echo "=========================================="
echo "Starting full build (CleanBuild = $CLEAN_BUILD)"
echo "=========================================="

# KIRHelperKit (publish to local Maven)
run_gradle "Building KIRHelperKit" "./KIRHelperKit" build publishToMavenLocal

# Plugins (publish to local Maven)
run_gradle "Building instrumentation-overhead-analyzer plugin" "./plugins/instrumentation-overhead-analyzer" build publishToMavenLocal
run_gradle "Building k-perf plugin" "./plugins/k-perf" build publishToMavenLocal

# KMP examples
run_kmp_build "Building game-of-life-kmp example" "./kmp-examples/game-of-life-kmp"
run_kmp_build "Building game-of-life-kmp-ioa example" "./kmp-examples/game-of-life-kmp-ioa"
run_kmp_build "Building game-of-life-kmp-k-perf example" "./kmp-examples/game-of-life-kmp-k-perf"

echo ""
echo "=========================================="
echo "All builds completed successfully."
echo "=========================================="
