val randomDefaultObjectClass = pluginContext.referenceClass(ClassId.fromString("kotlin/random/Random.Default"))!!
val nextIntFunc = pluginContext.referenceFunctions(
    CallableId(
        FqName("kotlin.random"),
        FqName("Random.Default"),
        Name.identifier("nextInt")
    )
).single {
    it.owner.valueParameters.isEmpty()
}
val firstFile = moduleFragment.files[0]

val randomNumber = pluginContext.irFactory.buildField {
    name = Name.identifier("_randNumber")
    type = pluginContext.irBuiltIns.intType
    isFinal = false
    isStatic = true
}.apply {
    initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
        irExprBody(irCall(nextIntFunc).apply {
            dispatchReceiver = irGetObject(randomDefaultObjectClass)
        })
    }
}