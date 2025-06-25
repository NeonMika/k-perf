# Uncomment version you want to run
$jarPath = "..\..\kmp-examples\game_of_life-plain\build\lib\kotlin-multiplatform-game-of-life-jvm-0.0.1.jar"
#$jarPath = "..\..\kmp-examples\game_of_life-tracing\lib\kotlin-multiplatform-game-of-life-jvm-0.0.1.jar"
#$exePath = "..\..\kmp-examples\game_of_life-plain\build\bin\mingwX64\releaseExecutable\kotlin-multiplatform-game-of-life.exe"
#$exePath = "..\..\kmp-examples\game_of_life-tracing\build\bin\mingwX64\releaseExecutable\kotlin-multiplatform-game-of-life.exe"
#$nodePath = "..\..\kmp-examples\game_of_life-plain\build\js\packages\kotlin-multiplatform-game-of-life\kotlin\kotlin-multiplatform-game-of-life.js"
#$nodePath = "..\..\kmp-examples\game_of_life-tracing\build\js\packages\kotlin-multiplatform-game-of-life\kotlin\kotlin-multiplatform-game-of-life.js"


# Initialize an array to hold the elapsed times
$elapsedTimes = @()

# Run the loop 100 times
for ($i = 1; $i -le 100; $i++) {
    Write-Host "Running iteration $i of 100..."

    # Run the program and capture the output
	# Uncomment respective line, based on whether you want to run Java, JS, or native
    $output = java -jar $jarPath
	# $output = & $exePath
	# $output = node $nodePath

    # Find the line that starts with "### Elapsed time: "
    $elapsedLine = $output | Select-String "^### Elapsed time: "

    if ($elapsedLine) {
        # Extract the number after "### Elapsed time: "
        $elapsedTime = $elapsedLine -replace "### Elapsed time:\s*", ""

        # Trim any whitespace
        $elapsedTime = $elapsedTime.Trim()

        # Add to the array
        $elapsedTimes += $elapsedTime
    } else {
        Write-Host "Elapsed time not found in iteration $i"
    }
}

# Write the elapsed times to a file
$elapsedTimes | Out-File -FilePath "elapsed_times.txt" -Encoding utf8

Write-Host "All elapsed times have been collected and saved to elapsed_times.txt"