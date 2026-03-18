#!/bin/bash

runs=100

# plugin
#jvm_command="java -jar ../plugin/build/lib/plugin-jvm-1.0.0.jar"
#jvm_file="plugin/jvm.txt"
#js_command="node ../plugin/build/js/packages/plugin/kotlin/plugin.js"
#js_file="plugin/js.txt"
#native_command="../plugin/build/bin/linuxX64/releaseExecutable/plugin.kexe"
#native_file="plugin/native.txt"

# manual
#jvm_command="java -jar ../manual/build/lib/manual-jvm-1.0.0.jar"
#jvm_file="manual/jvm.txt"
#js_command="node ../manual/build/js/packages/manual/kotlin/manual.js"
#js_file="manual/js.txt"
#native_command="../manual/build/bin/linuxX64/releaseExecutable/manual.kexe"
#native_file="manual/native.txt"

# manual-noop
#jvm_command="java -jar ../manual-noop/build/lib/manual_noop-jvm-1.0.0.jar"
#jvm_file="manual-noop/jvm.txt"
#js_command="node ../manual-noop/build/js/packages/manual_noop/kotlin/manual_noop.js"
#js_file="manual-noop/js.txt"
#native_command="../manual-noop/build/bin/linuxX64/releaseExecutable/manual_noop.kexe"
#native_file="manual-noop/native.txt"

# reference
#jvm_command="java -jar ../reference/build/lib/reference-jvm-1.0.0.jar"
#jvm_file="reference/jvm.txt"
#js_command="node ../reference/build/js/packages/reference/kotlin/reference.js"
#js_file="reference/js.txt"
#native_command="../reference/build/bin/linuxX64/releaseExecutable/reference.kexe"
#native_file="reference/native.txt"

run() {
    command="$1"
    file="$2"

    > $file
    for i in $(seq 1 $runs); do
        echo "run $i / $runs"
        output=$($command 2>&1)
        execution_time=$(echo "$output" | grep "Execution finished" | sed -r 's/Execution finished - ([0-9]*) ms elapsed/\1/')
        flush_time=$(echo "$output" | grep "Flush finished" | sed -r 's/Flush finished - ([0-9]*) ms elapsed/\1/')
        echo "$execution_time $flush_time" >> "$file"
    done
}

echo "--- jvm ---"
run "$jvm_command" "$jvm_file"
echo "--- js ---"
run "$js_command" "$js_file"
echo "--- linux ---"
run "$native_command" "$native_file"
