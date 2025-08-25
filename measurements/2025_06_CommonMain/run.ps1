# Define a list of all executables to be benchmarked.
# Each item now has a 'Name' property for creating a unique and readable output file.
$executables = @(
    @{ Name = "plain_jar";    Path = "..\..\kmp-examples\game_of_life-plain\build\lib\kotlin-multiplatform-game-of-life-jvm-0.0.2.jar";                                         Type = "jar" },
    @{ Name = "tracing_jar";  Path = "..\..\kmp-examples\game_of_life-tracing\build\lib\kotlin-multiplatform-game-of-life-jvm-0.0.2.jar";                                             Type = "jar" },
    @{ Name = "plain_exe";    Path = "..\..\kmp-examples\game_of_life-plain\build\bin\mingwX64\releaseExecutable\kotlin-multiplatform-game-of-life.exe";                        Type = "exe" },
    @{ Name = "tracing_exe";  Path = "..\..\kmp-examples\game_of_life-tracing\build\bin\mingwX64\releaseExecutable\kotlin-multiplatform-game-of-life.exe";                      Type = "exe" },
    @{ Name = "plain_node";   Path = "..\..\kmp-examples\game_of_life-plain\build\js\packages\kotlin-multiplatform-game-of-life\kotlin\kotlin-multiplatform-game-of-life.js";   Type = "node" },
    @{ Name = "tracing_node"; Path = "..\..\kmp-examples\game_of_life-tracing\build\js\packages\kotlin-multiplatform-game-of-life\kotlin\kotlin-multiplatform-game-of-life.js"; Type = "node" }
)

# Loop through each executable defined above
foreach ($executable in $executables) {
    
    $filePath = $executable.Path
    $fileType = $executable.Type

    Write-Host "--------------------------------------------------------"
    Write-Host "Starting benchmark for: $($executable.Name) ($filePath)"
    Write-Host "--------------------------------------------------------"

    # Initialize an array to hold the elapsed times for the current file
    $elapsedTimes = @()

    # Run the loop 10 times
    for ($i = 1; $i -le 10; $i++) {
        Write-Host "Running iteration $i of 100 for $($executable.Name):"

        # Run the program and capture the output based on its type
        $output = $null
        switch ($fileType) {
            "jar"  { $output = java -jar $filePath }
            "exe"  { $output = & $filePath }
            "node" { $output = node $filePath }
        }

        # Find the line that starts with "### Elapsed time: "
        $elapsedLine = $output | Select-String "^### Elapsed time: "

        if ($elapsedLine) {
            # Extract the number after "### Elapsed time: "
            $elapsedTime = $elapsedLine -replace "### Elapsed time:\s*", ""

            # Trim any whitespace
            $elapsedTime = $elapsedTime.Trim()

            # Print the result for the current iteration
            Write-Host "$elapsedTime"

            # Add to the array
            $elapsedTimes += $elapsedTime
        } else {
            Write-Host "Elapsed time not found in iteration $i"
        }
    }

    # Generate the output file name using the descriptive 'Name' property.
    $outputFileName = "elapsed_times_$($executable.Name).txt"

    # Write the elapsed times to a unique file
    $elapsedTimes | Out-File -FilePath $outputFileName -Encoding utf8

    Write-Host "All elapsed times for this run have been collected and saved to $outputFileName"
    Write-Host ""
}

Write-Host "All benchmarks are complete."