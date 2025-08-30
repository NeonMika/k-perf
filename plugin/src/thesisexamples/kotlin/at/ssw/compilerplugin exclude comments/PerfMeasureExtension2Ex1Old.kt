val funMarkNow = pluginContext.referenceFunctions(
    CallableId(
        FqName("kotlin.time"),
        FqName("TimeSource.Monotonic"),
        Name.identifier("markNow")
    )
).single()