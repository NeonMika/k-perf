
# Simplifying Kotlin Compiler Plugin Development

This project aims to simplify and enhance the process of developing Kotlin compiler plugins. It provides a set of tools and utilities designed to make working with Kotlin's Intermediate Representation (IR) more accessible and efficient.

## What it Does

This project streamlines various aspects of Kotlin compiler plugin development, focusing on the following key features:

*   **IR Element Finding (CompilerPluginFindUtils.kt):** This module provides helper functions to easily locate various elements within the Kotlin IR.
    *   `findClass`: Locates an `IrClassSymbol` based on its signature, allowing you to find classes within the IR tree. For example, `findClass("kotlin/io/MyClass")` can be used to find a specific class.
    *   `findFunction`: Retrieves an `IrSimpleFunctionSymbol` for a given function signature globally, within a class or extension. It supports finding functions by name and parameter types, such as `stringBuilderClass.findFunction("append(Int)")` to find the `append` method of a `StringBuilder` instance.
    *   `findProperty`: Identifies an `IrPropertySymbol` by its name within a given class or globally, simplifying access to properties in the IR.

*   **IR Construction with DSL (CompilerPluginFunctionUtils.kt):** This module simplifies the construction of Kotlin IR elements using a Domain-Specific Language (DSL).
    *   `enableCallDSL`: Enables a DSL for constructing IR calls within an IR block body, making IR code generation more fluent and readable.
    *   `callExpression`: Facilitates the construction of IR expression bodies, allowing you to build complex IR structures programmatically.
    *   `getCall`: Used to return IR calls for further work.

*   **Simplified IR Utilities (ComiplerPluginGeneralUtils.kt):** This module provides simplified utilities for common tasks within Kotlin IR.
    *   `IrStringBuilder`: Offers a simpler way to manage a `StringBuilder` within the Kotlin IR context, providing methods like `append()`, `insert()`, and `delete()` that generate the corresponding IR operations.
    *   `IrFileIOHandler`: Provides simplified file input/output operations within the Kotlin IR, abstracting away the complexities of direct file manipulation in the IR.

## How to Use

To use this compiler plugin in your Kotlin projects, follow these steps:

### 1. Configure JDK in `gradle.properties`

Before building, ensure your `gradle.properties` file points to the correct Java Development Kit (JDK) installation. This is crucial for Gradle to function correctly.

**Important:** Update the path `C:/Users/User/.jdks/jdk.version` to the actual location of your JDK.

### 2. Build the Project

Navigate to the root directory of this project in your terminal and execute the following Gradle command to build the project:
bash
./gradlew build

This command compiles the source code, runs tests, and packages the plugin.

### 3. Publish to Maven Local

After a successful build, publish the plugin to your local Maven repository. This makes the plugin accessible to other projects on your machine.
bash
./gradlew publishToMavenLocal

This command will install the plugin's artifacts (including the `KIRHelperKit` AAR and sources JAR) into your local Maven repository, typically located at `~/.m2/repository`.

### 4. Include the Plugin in Your Project

To use this compiler plugin in another Kotlin project, you need to declare it as a dependency in that project's `build.gradle.kts` file.

First, ensure your project's `settings.gradle.kts` includes `mavenLocal()` in its plugin repositories:

```
// settings.gradle.kts
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal() // Add this line
    }
}
```


Then, in the `build.gradle.kts` of the project where you want to use the plugin, apply the plugin and add the necessary dependencies. Replace `"at.ssw"` and `"KIRHelperKit"` with your actual `groupId` and `artifactId` if they differ.
```
// build.gradle.kts of your consuming project
plugins {
    kotlin("jvm") version "2.0.20" // Or your desired Kotlin version
    id("at.ssw.KIRHelperKit") version "0.0.2" // Apply your plugin
}

repositories {
    mavenCentral()
    mavenLocal() // Make sure mavenLocal is included here as well
}

dependencies {
    // Other dependencies
    implementation(kotlin("stdlib"))
}
```

Now, your project will have access to the functionalities provided by the `KIRHelperKit` compiler plugin.html

