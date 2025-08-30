val randomDefaultObjectClass = pluginContext.findClass("kotlin/random/Random.Default")
val nextIntFunc = pluginContext.findFunction("kotlin/random/Random.Default.nextInt()")
val firstFile = moduleFragment.files[0]
val randomNumber = pluginContext.irFactory.buildField {
    name = Name.identifier("_randNumber")
    type = pluginContext.irBuiltIns.intType
    isFinal = false
    isStatic = true
}.apply {
    initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
        irExprBody(call(pluginContext, nextIntFunc, irGetObject(randomDefaultObjectClass)))
    }
}