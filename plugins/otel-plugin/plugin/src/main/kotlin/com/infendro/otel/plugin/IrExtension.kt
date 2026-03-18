package com.infendro.otel.plugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.IrValueParameterBuilder
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrTransformer
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.jvm.JvmPlatform

class IrExtension(
    val debug: Boolean,
    val host: String?,
    val service: String?,
) : IrGenerationExtension {
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext
    ) = with(pluginContext) {
        val platform = platform!!.single()

        val firstFile = moduleFragment.files[0]

        // region helpers
        val unit = irBuiltIns.unitType
        val any = irBuiltIns.anyType
        val int = irBuiltIns.intType
        val long = irBuiltIns.longType
        val float = irBuiltIns.floatType
        val double = irBuiltIns.doubleType
        val boolean = irBuiltIns.booleanType
        val char = irBuiltIns.charType
        val string = irBuiltIns.stringType

        fun getClass(
            packageName: String,
            name: String
        ): IrClassSymbol {
            val classId = ClassId(FqName(packageName), Name.identifier(name))
            return referenceClass(classId)
                ?: throw Exception("class \"${packageName}/${name}\" not found")
        }

        fun getFunction(
            packageName: String,
            name: String,
            filter: (IrFunction) -> Boolean = { true }
        ): IrSimpleFunctionSymbol {
            val callableId = CallableId(FqName(packageName), Name.identifier(name))
            return referenceFunctions(callableId).singleOrNull { filter(it.owner) }
                ?: throw Exception("function \"${packageName}/${name}\" not found")
        }

        fun IrClassSymbol.type(): IrType {
            return defaultType
        }

        fun IrClassSymbol.getClass(
            name: String
        ): IrClassSymbol {
            return owner.declarations
                .filterIsInstance<IrClass>()
                .single { it.name.toString() == name }
                .symbol
        }

        fun IrClassSymbol.getConstructor(
            filter: (IrConstructor) -> Boolean = { it.isPrimary }
        ): IrConstructorSymbol {
            return owner.declarations
                .filterIsInstance<IrConstructor>()
                .single(filter)
                .symbol
        }

        fun IrClassSymbol.getFunction(
            name: String,
            filter: (IrFunction) -> Boolean = { true }
        ): IrSimpleFunctionSymbol {
            return owner.declarations
                .filterIsInstance<IrSimpleFunction>()
                .single { it.name.toString() == name && filter(it) }
                .symbol
        }

        val fields = mutableListOf<IrField>()
        val functions = mutableListOf<IrFunction>()

        fun buildField(
            name: String,
            type: IrType,
            final: Boolean = false,
            static: Boolean = false,
            block: IrField.() -> Unit = {}
        ): IrField {
            return irFactory
                .buildField {
                    this.name = Name.identifier(name)
                    this.type = type
                    this.isFinal = final
                    this.isStatic = static
                }
                .apply(block)
                .also(fields::add)
        }

        fun buildFunction(
            name: String,
            returnType: IrType = unit,
            block: IrSimpleFunction.() -> Unit = {}
        ): IrSimpleFunction {
            return irFactory
                .buildFun {
                    this.name = Name.identifier(name)
                    this.returnType = returnType
                }
                .apply(block)
                .also(functions::add)
        }

        fun IrFunction.parameter(
            block: IrValueParameterBuilder.() -> Unit
        ) {
            addValueParameter {
                block()
            }
        }

        fun expression(
            block: IrBuilderWithScope.() -> IrExpression
        ): IrExpressionBody {
            val builder = DeclarationIrBuilder(pluginContext, firstFile.symbol)
            return builder.irExprBody(builder.block())
        }

        fun IrFunction.body(
            block: IrBlockBodyBuilder.() -> Unit
        ) {
            val builder = DeclarationIrBuilder(pluginContext, symbol)
            body = builder.irBlockBody {
                block()
            }
        }

        fun IrBuilderWithScope.call(
            function: IrSimpleFunctionSymbol,
            block: IrCall.() -> Unit = {}
        ): IrCall {
            return irCall(function).apply(block)
        }

        fun IrBuilderWithScope.call(
            function: IrSimpleFunction,
            block: IrCall.() -> Unit = {}
        ) = call(function.symbol, block)

        fun IrBuilderWithScope.call(
            constructor: IrConstructorSymbol,
            block: IrConstructorCall.() -> Unit = {}
        ): IrConstructorCall {
            return irCall(constructor).apply(block)
        }

        fun IrBuilderWithScope.call(
            constructor: IrConstructor,
            block: IrConstructorCall.() -> Unit = {}
        ) = call(constructor.symbol, block)

        fun IrCall.argument(
            index: Int,
            value: IrExpression?
        ) {
            putValueArgument(index, value)
        }

        fun IrConstructorCall.argument(
            index: Int,
            value: IrExpression?
        ) {
            putValueArgument(index, value)
        }

        fun IrFunction.isMain() = name.toString() == "main"
        // endregion

        // region
        val println = getFunction("kotlin.io", "println") {
            it.valueParameters.size == 1
                && it.valueParameters[0].type == any.makeNullable()
        }

        val StringBuilder = when (platform) {
            is JvmPlatform -> getClass("java.lang", "StringBuilder")
            else -> getClass("kotlin.text", "StringBuilder")
        }
        val StringBuilder_constructor = StringBuilder.getConstructor {
            it.valueParameters.isEmpty()
        }
        val StringBuilder_appendString = StringBuilder.getFunction("append") {
            it.valueParameters.size == 1
                && it.valueParameters[0].type == string.makeNullable()
        }
        val StringBuilder_appendLong = StringBuilder.getFunction("append") {
            it.valueParameters.size == 1
                && it.valueParameters[0].type == long
        }
        val StringBuilder_toString = StringBuilder.getFunction("toString")

        val Duration = getClass(
            "kotlin.time",
            "Duration"
        )
        val Duration_inWholeMilliseconds = Duration.getPropertyGetter("inWholeMilliseconds")!!

        val Instant = getClass(
            "kotlinx.datetime",
            "Instant"
        )
        val Instant_minus = Instant.getFunction("minus") {
            it.valueParameters.size == 1
                && it.valueParameters[0].type == Instant.type()
        }

        val Clock = getClass(
            "kotlinx.datetime",
            "Clock"
        )
        val System = Clock.getClass("System")
        val now = System.getFunction("now")

        val Exporter = getClass("com.infendro.otlp", "OtlpExporter")
        val Exporter_constructor = Exporter.getConstructor()

        val Processor = getClass(
            "io.opentelemetry.kotlin.sdk.trace.export",
            "BatchSpanProcessor"
        )
        val Processor_shutdown = Processor.getFunction("shutdown")
        val ProcessorCompanion = Processor.getClass("Companion")
        val ProcessorCompanion_builder = ProcessorCompanion.getFunction("builder")

        val ProcessorBuilder = getClass(
            "io.opentelemetry.kotlin.sdk.trace.export",
            "BatchSpanProcessorBuilder"
        )
        val ProcessorBuilder_setMaxQueueSize = ProcessorBuilder.getFunction("setMaxQueueSize")
        val ProcessorBuilder_setMaxExportBatchSize = ProcessorBuilder.getFunction("setMaxQueueSize")
        val ProcessorBuilder_build = ProcessorBuilder.getFunction("build")

        val TracerProvider = getClass(
            "io.opentelemetry.kotlin.sdk.trace",
            "SdkTracerProvider"
        )
        val TracerProvider_tracerBuilder = TracerProvider.getFunction("tracerBuilder")
        val TracerProviderCompanion = TracerProvider.getClass("Companion")
        val TracerProviderCompanion_builder = TracerProviderCompanion.getFunction("builder")

        val TracerProviderBuilder = getClass(
            "io.opentelemetry.kotlin.sdk.trace",
            "SdkTracerProviderBuilder"
        )
        val TracerProviderBuilder_addSpanProcessor = TracerProviderBuilder.getFunction("addSpanProcessor")
        val TracerProviderBuilder_build = TracerProviderBuilder.getFunction("build")

        val TracerBuilder = getClass(
            "io.opentelemetry.kotlin.api.trace",
            "TracerBuilder"
        )
        val TracerBuilder_build = TracerBuilder.getFunction("build")

        val Tracer = getClass("io.opentelemetry.kotlin.api.trace", "Tracer")
        val Tracer_spanBuilder = Tracer.getFunction("spanBuilder")

        val Context = getClass("io.opentelemetry.kotlin.context", "Context")
        val ImplicitContextKeyed = getClass(
            "io.opentelemetry.kotlin.context",
            "ImplicitContextKeyed"
        )
        val Context_with = Context.getFunction("with") {
            it.valueParameters.size == 1 && it.valueParameters[0].type == ImplicitContextKeyed.type()
        }
        val Context_makeCurrent = Context.getFunction("makeCurrent")
        val ContextCompanion = Context.getClass("Companion")
        val ContextCompanion_current = ContextCompanion.getFunction("current")

        val SpanBuilder = getClass("io.opentelemetry.kotlin.api.trace", "SpanBuilder")
        val SpanBuilder_setParent = SpanBuilder.getFunction("setParent")
        val SpanBuilder_setStartTimestamp = SpanBuilder.getFunction("setStartTimestamp") {
            it.valueParameters.size == 1 && it.valueParameters[0].type == Instant.type()
        }
        val SpanBuilder_startSpan = SpanBuilder.getFunction("startSpan")

        val Span = getClass("io.opentelemetry.kotlin.api.trace", "Span")
        val Span_end = Span.getFunction("end") {
            it.valueParameters.size == 1 && it.valueParameters[0].type == Instant.type()
        }

        val await = getFunction("com.infendro.otel.util", "await") {
            it.valueParameters.size == 1
                && it.valueParameters[0].type == Exporter.type()
        }
        val await_debug = getFunction("com.infendro.otel.util", "await") {
            it.valueParameters.size == 2
                && it.valueParameters[0].type == Exporter.type()
                && it.valueParameters[1].type == Instant.type()
        }
        val env = getFunction("com.infendro.otel.util", "env")
        // endregion

        // region fields
        val host = if (host != null) {
            // val host = <host>
            buildField(
                name = "_host",
                type = string,
                static = true,
            ) {
                initializer = expression {
                    irString(host)
                }
            }
        } else {
            // val hostEnv = env("OTLP_HOST")
            val hostEnv = buildField(
                name = "_hostEnv",
                type = string,
                static = true,
            ) {
                initializer = expression {
                    call(env) {
                        argument(0, irString("OTLP_HOST"))
                    }
                }
            }

            // val host = if(hostEnv != null) hostEnv else "localhost:4318"
            buildField(
                name = "_host",
                type = string,
                static = true,
            ) {
                initializer = expression {
                    irIfThenElse(
                        type = string,
                        condition = irNotEquals(
                            irGetField(null, hostEnv),
                            irNull()
                        ),
                        thenPart = irGetField(null, hostEnv),
                        elsePart = irString("localhost:4318")
                    )
                }
            }
        }

        val service = if (service != null) {
            // val service = <service>
            buildField(
                name = "_service",
                type = string,
                static = true,
            ) {
                initializer = expression {
                    irString(service)
                }
            }
        } else {
            // val serviceEnv = env("OTLP_SERVICE")
            val serviceEnv = buildField(
                name = "_serviceEnv",
                type = string,
                static = true,
            ) {
                initializer = expression {
                    call(env) {
                        argument(0, irString("OTLP_SERVICE"))
                    }
                }
            }

            // val service = if(serviceEnv != null) serviceEnv else ""
            buildField(
                name = "_service",
                type = string,
                static = true,
            ) {
                initializer = expression {
                    irIfThenElse(
                        type = string,
                        condition = irNotEquals(
                            irGetField(null, serviceEnv),
                            irNull()
                        ),
                        thenPart = irGetField(null, serviceEnv),
                        elsePart = irString("")
                    )
                }
            }
        }

        // val exporter = OtlpExporter(host, service)
        val exporter = buildField(
            name = "_exporter",
            type = Exporter.type(),
            static = true,
        ) {
            initializer = expression {
                call(Exporter_constructor) {
                    argument(0, irGetField(null, host))
                    argument(1, irGetField(null, service))
                }
            }
        }

        // val processor = BatchSpanProcessor
        //     .builder(exporter)
        //     .setMaxQueueSize(Int.MAX_VALUE)
        //     .setMaxExportBatchSize(2048)
        //     .build()
        val processor = buildField(
            name = "_processor",
            type = Processor.type(),
            static = true,
        ) {
            initializer = expression {
                call(ProcessorBuilder_build) {
                    dispatchReceiver = call(ProcessorBuilder_setMaxExportBatchSize) {
                        argument(0, irInt(Int.MAX_VALUE))
                        dispatchReceiver = call(ProcessorBuilder_setMaxQueueSize) {
                            argument(0, irInt(2048))
                            dispatchReceiver = call(ProcessorCompanion_builder) {
                                argument(0, irGetField(null, exporter))
                                dispatchReceiver = irGetObject(ProcessorCompanion)
                            }
                        }
                    }
                }
            }
        }

        // val provider = SdkTracerProvider.builder().addSpanProcessor(processor).build()
        val provider = buildField(
            name = "_provider",
            type = TracerProvider.type(),
            static = true
        ) {
            initializer = expression {
                call(TracerProviderBuilder_build) {
                    dispatchReceiver = call(TracerProviderBuilder_addSpanProcessor) {
                        dispatchReceiver = call(TracerProviderCompanion_builder) {
                            dispatchReceiver = irGetObject(TracerProviderCompanion)
                        }
                        argument(0, irGetField(null, processor))
                    }
                }
            }
        }

        // val tracer = provider.tracerBuilder("").build()
        val tracer = buildField(
            name = "_tracer",
            type = Tracer.type(),
            static = true
        ) {
            initializer = expression {
                call(TracerBuilder_build) {
                    dispatchReceiver = call(TracerProvider_tracerBuilder) {
                        dispatchReceiver = irGetField(null, provider)
                        argument(0, irString(""))
                    }
                }
            }
        }

        firstFile.addChildren(fields)
        // endregion

        // region functions
        val startSpan = buildFunction("_startSpan") {
            parameter {
                name = Name.identifier("name")
                type = string
            }
            parameter {
                name = Name.identifier("context")
                type = Context.type()
            }
            returnType = Span.type()

            body {
                val name = valueParameters[0]
                val context = valueParameters[1]

                // val spanBuilder = tracer.spanBuilder(name)
                val spanBuilder = irTemporary(
                    call(Tracer_spanBuilder) {
                        dispatchReceiver = irGetField(null, tracer)
                        argument(0, irGet(name))
                    }
                )

                // spanBuilder.setParent(context)
                +call(SpanBuilder_setParent) {
                    dispatchReceiver = irGet(spanBuilder)
                    argument(0, irGet(context))
                }

                // spanBuilder.setStartTimestamp(Clock.System.now())
                +call(SpanBuilder_setStartTimestamp) {
                    dispatchReceiver = irGet(spanBuilder)
                    argument(
                        0,
                        call(now) {
                            dispatchReceiver = irGetObject(System)
                        }
                    )
                }

                // val span = spanBuilder.startSpan()
                val span = irTemporary(
                    call(SpanBuilder_startSpan) {
                        dispatchReceiver = irGet(spanBuilder)
                    }
                )

                // context.with(span).makeCurrent()
                +call(Context_makeCurrent) {
                    dispatchReceiver = call(Context_with) {
                        dispatchReceiver = irGet(context)
                        argument(0, irGet(span))
                    }
                }

                // return span
                +irReturn(irGet(span))
            }
        }

        val endSpan = buildFunction("_endSpan") {
            parameter {
                name = Name.identifier("span")
                type = Span.type()
            }
            parameter {
                name = Name.identifier("context")
                type = Context.type()
            }
            returnType = unit

            body {
                // context.makeCurrent()
                +call(Context_makeCurrent) {
                    dispatchReceiver = irGet(valueParameters[1])
                }

                // span.end(Clock.System.now())
                +call(Span_end) {
                    dispatchReceiver = irGet(valueParameters[0])
                    argument(
                        0,
                        call(now) {
                            dispatchReceiver = irGetObject(System)
                        }
                    )
                }
            }
        }

        firstFile.addChildren(functions)
        // endregion

        fun IrFunction.modify() {
            body {
                var start: IrVariable? = null
                if (isMain() && debug) {
                    // val start = Clock.System.now()
                    start = irTemporary(
                        call(now) {
                            dispatchReceiver = irGetObject(System)
                        }
                    )
                }

                // val context = Context.current()
                val context = irTemporary(
                    call(ContextCompanion_current) {
                        dispatchReceiver = irGetObject(ContextCompanion)
                    }
                )
                // val span = _startSpan(<function name>, context)
                val span = irTemporary(
                    call(startSpan) {
                        argument(0, irString(name.toString()))
                        argument(1, irGet(context))
                    }
                )

                val tryBlock: IrExpression = irBlock(
                    resultType = returnType
                ) {
                    for (statement in body!!.statements) +statement
                }

                +irTry(
                    tryBlock.type,
                    tryBlock,
                    listOf(),
                    irBlock {
                        // _endSpan(span, context)
                        +call(endSpan) {
                            argument(0, irGet(span))
                            argument(1, irGet(context))
                        }

                        if (isMain()) {
                            if (debug) {
                                // val end = Clock.System.now()
                                val end = irTemporary(
                                    call(now) {
                                        dispatchReceiver = irGetObject(System)
                                    }
                                )

                                // val ms = (end - start).inWholeMilliseconds
                                val duration = irTemporary(
                                    call(Instant_minus) {
                                        dispatchReceiver = irGet(end)
                                        argument(0, irGet(start!!))
                                    }
                                )
                                val ms = irTemporary(
                                    call(Duration_inWholeMilliseconds) {
                                        dispatchReceiver = irGet(duration)
                                    }
                                )

                                // println("Execution finished - $ms ms elapsed")
                                val builder = irTemporary(
                                    call(StringBuilder_constructor)
                                )
                                +call(StringBuilder_appendString) {
                                    dispatchReceiver = irGet(builder)
                                    argument(0, irString("Execution finished - "))
                                }
                                +call(StringBuilder_appendLong) {
                                    dispatchReceiver = irGet(builder)
                                    argument(0, irGet(ms))
                                }
                                +call(StringBuilder_appendString) {
                                    dispatchReceiver = irGet(builder)
                                    argument(0, irString(" ms elapsed"))
                                }
                                val string = irTemporary(
                                    call(StringBuilder_toString) {
                                        dispatchReceiver = irGet(builder)
                                    }
                                )
                                +call(println) {
                                    argument(0, irGet(string))
                                }
                            }

                            // processor.shutdown()
                            +call(Processor_shutdown) {
                                dispatchReceiver = irGetField(null, processor)
                            }

                            if (debug) {
                                // await(exporter, start)
                                +call(await_debug) {
                                    argument(0, irGetField(null, exporter))
                                    argument(1, irGet(start!!))
                                }
                            } else {
                                // await(exporter)
                                +call(await) {
                                    argument(0, irGetField(null, exporter))
                                }
                            }
                        }
                    }
                )
            }
        }

        // region modify functions
        moduleFragment.files.forEach { file ->
            file.transform(
                object : IrTransformer<Any?>() {
                    override fun visitFunction(declaration: IrFunction, data: Any?): IrStatement {
                        fun shouldModify(): Boolean {
                            val invalidOrigins = listOf<IrDeclarationOrigin>(
                                IrDeclarationOrigin.ADAPTER_FOR_CALLABLE_REFERENCE, // function references using :: operator
                            )

                            val name = declaration.name.toString()
                            val body = declaration.body
                            val origin = declaration.origin
                            return declaration !in functions &&    // is not a generated function
                                !name.startsWith("_") &&   // is not an ignored function
                                body != null &&                    // has a body
                                name != "<init>" &&                // is not a constructor
                                name != "<anonymous>" &&           // is not an anonymous function
                                !declaration.isGetter &&
                                !declaration.isSetter &&
                                origin !in invalidOrigins
                        }

                        if (shouldModify()) declaration.modify()
                        return super.visitFunction(declaration, data)
                    }
                },
                null
            )

            if (debug) {
                println("---${file.name}---")
                println(file.dump())
            }
        }
        // endregion
    }
}
