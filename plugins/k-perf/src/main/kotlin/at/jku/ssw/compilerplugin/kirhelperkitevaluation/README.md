# What is this directory?

This directory was created as part of **the evaluation of Lorenz Bader's Bachelor's Thesis "KIRHelperKit: A Utility Toolkit for Simplified Kotlin IR Generation"**.

**KIRHelperKit** is also part of this repository and is a toolkit to **simplify Kotlin IR generation**.

To **evaluate how much lines of code can be saved**, this directory contains three versions of k-perf as of late 2025:

- `KIRHelperKitEvaluation_Original.kt`: The original k-perf extension implemented without using **KIRHelperKit**:
- `KIRHelperKitEvaluation_FunctionUtils.kt`: k-perf implemented using **KIRHelperKit**'s `findUtils` and `callUtils`, but no `generalUtils`.
- `KIRHelperKitEvaluation_GeneralUtils.kt`: k-perf implement using all of **KIRHelperKit**'s utilities.