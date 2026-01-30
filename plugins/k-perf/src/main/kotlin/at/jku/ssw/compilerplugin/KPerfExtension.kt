package at.jku.ssw.compilerplugin

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin.Companion.ADAPTER_FOR_CALLABLE_REFERENCE
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.addArgument
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.presentableDescription
import java.io.File
import kotlin.time.ExperimentalTime

class KPerfExtension(
  private val messageCollector: MessageCollector
) : IrGenerationExtension {

  val STRINGBUILDER_MODE = false

  val debugFile = File("./DEBUG.txt")

  init {
    debugFile.delete()
  }

  fun appendToDebugFile(str: String) {
    debugFile.appendText(str)
  }

  @OptIn(UnsafeDuringIrConstructionAPI::class, ExperimentalTime::class)
  override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
    val timeMarkClass: IrClassSymbol =
      pluginContext.referenceClass(ClassId.fromString("kotlin/time/TimeMark"))!!

    val stringBuilderClassId = ClassId.fromString("kotlin/text/StringBuilder")
    // In JVM, StringBuilder is a type alias (see https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-string-builder/)
    val stringBuilderTypeAlias = pluginContext.referenceTypeAlias(stringBuilderClassId)
    val stringBuilderClass = stringBuilderTypeAlias?.owner?.expandedType?.classOrFail
      ?: pluginContext.referenceClass(stringBuilderClassId)!! // In native and JS, StringBuilder is a class


    val stringBuilderConstructor =
      stringBuilderClass.constructors.single { it.owner.valueParameters.isEmpty() }
    val stringBuilderAppendIntFunc =
      stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.intType }
    val stringBuilderAppendLongFunc =
      stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.longType }
    val stringBuilderAppendStringFunc =
      stringBuilderClass.functions.single { it.owner.name.asString() == "append" && it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].type == pluginContext.irBuiltIns.stringType.makeNullable() }

    val printlnFunc =
      pluginContext.referenceFunctions(CallableId(FqName("kotlin.io"), Name.identifier("println"))).single {
        it.owner.valueParameters.run { size == 1 && get(0).type == pluginContext.irBuiltIns.anyNType }
      }

    val rawSinkClass =
      pluginContext.referenceClass(ClassId.fromString("kotlinx/io/RawSink"))!!

    // Watch out, Path does not use constructors but functions to build
    val pathConstructionFunc = pluginContext.referenceFunctions(
      CallableId(
        FqName("kotlinx.io.files"),
        Name.identifier("Path")
      )
    ).single { it.owner.valueParameters.size == 1 }

    val systemFileSystem = pluginContext.referenceProperties(
      CallableId(
        FqName("kotlinx.io.files"),
        Name.identifier("SystemFileSystem")
      )
    ).single()
    val systemFileSystemClass = systemFileSystem.owner.getter!!.returnType.classOrFail
    val sinkFunc = systemFileSystemClass.functions.single { it.owner.name.asString() == "sink" }
    val bufferedFunc = pluginContext.referenceFunctions(
      CallableId(
        FqName("kotlinx.io"),
        Name.identifier("buffered")
      )
    ).single { it.owner.extensionReceiverParameter!!.type == sinkFunc.owner.returnType }
    appendToDebugFile("Different versions of kotlinx.io.writeString:\n")
    appendToDebugFile(
      pluginContext.referenceFunctions(
        CallableId(
          FqName("kotlinx.io"),
          Name.identifier("writeString")
        )
      ).joinToString("\n") { func ->
        "kotlinx.io.writeString(${func.owner.valueParameters.joinToString(",") { param -> param.type.classFqName.toString() }})"
      }
    )
    val writeStringFunc = pluginContext.referenceFunctions(
      CallableId(
        FqName("kotlinx.io"),
        Name.identifier("writeString")
      )
    ).single {
      it.owner.valueParameters.size == 3 &&
              it.owner.valueParameters[0].type == pluginContext.irBuiltIns.stringType &&
              it.owner.valueParameters[1].type == pluginContext.irBuiltIns.intType &&
              it.owner.valueParameters[2].type == pluginContext.irBuiltIns.intType
    }
    val flushFunc = pluginContext.referenceFunctions(
      CallableId(
        FqName("kotlinx.io"),
        FqName("Sink"),
        Name.identifier("flush")
      )
    ).single()
    debugFile.appendText("2")
    val toStringFunc = pluginContext.referenceFunctions(
      CallableId(
        FqName("kotlin"),
        Name.identifier("toString")
      )
    ).single()
    debugFile.appendText("3")

    val firstFile = moduleFragment.files[0]

    val stringBuilder: IrField = pluginContext.irFactory.buildField {
      name = Name.identifier("_stringBuilder")
      type = stringBuilderClass.defaultType
      isFinal = false
      isStatic = true
    }.apply {
      this.initializer =
        DeclarationIrBuilder(pluginContext, firstFile.symbol).irExprBody(
          DeclarationIrBuilder(pluginContext, firstFile.symbol).irCallConstructor(
            stringBuilderConstructor,
            listOf()
          )
        )
    }
    firstFile.declarations.add(stringBuilder)
    stringBuilder.parent = firstFile

    val randomDefaultObjectClass =
      pluginContext.referenceClass(ClassId.fromString("kotlin/random/Random.Default"))!!
    val nextIntFunc = pluginContext.referenceFunctions(
      CallableId(
        FqName("kotlin.random"),
        FqName("Random.Default"),
        Name.identifier("nextInt")
      )
    ).single {
      it.owner.valueParameters.isEmpty()
    }

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
    firstFile.declarations.add(randomNumber)
    randomNumber.parent = firstFile

    val bufferedTraceFileName = pluginContext.irFactory.buildField {
      name = Name.identifier("_bufferedTraceFileName")
      type = pluginContext.irBuiltIns.stringType
      isFinal = false
      isStatic = true
    }.apply {
      initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
        irExprBody(
          irConcat().apply {
            addArgument(irString("./trace_${pluginContext.platform!!.presentableDescription}_"))
            // TODO: use kotlinx.datetime.Clock.System.now()
            addArgument(irGetField(null, randomNumber))
            addArgument(irString(".txt"))
          })
      }
    }
    firstFile.declarations.add(bufferedTraceFileName)
    bufferedTraceFileName.parent = firstFile

    val bufferedTraceFileSink = pluginContext.irFactory.buildField {
      name = Name.identifier("_bufferedTraceFileSink")
      type = rawSinkClass.defaultType
      isFinal = false
      isStatic = true
    }.apply {
      initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
        irExprBody(irCall(bufferedFunc).apply {
          extensionReceiver = irCall(sinkFunc).apply {
            dispatchReceiver = irCall(systemFileSystem.owner.getter!!)
            putValueArgument(
              0,
              irCall(pathConstructionFunc).apply {
                putValueArgument(0, irGetField(null, bufferedTraceFileName))
              })
          }
        })
      }
    }
    firstFile.declarations.add(bufferedTraceFileSink)
    bufferedTraceFileSink.parent = firstFile

    val bufferedSymbolsFileName = pluginContext.irFactory.buildField {
      name = Name.identifier("_bufferedSymbolsFileName")
      type = pluginContext.irBuiltIns.stringType
      isFinal = false
      isStatic = true
    }.apply {
      initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
        irExprBody(
          irConcat().apply {
            addArgument(irString("./symbols_${pluginContext.platform!!.presentableDescription}_"))
            // TODO: use kotlinx.datetime.Clock.System.now()
            addArgument(irGetField(null, randomNumber))
            addArgument(irString(".txt"))
          })
      }
    }
    firstFile.declarations.add(bufferedSymbolsFileName)
    bufferedSymbolsFileName.parent = firstFile


    val bufferedSymbolsFileSink = pluginContext.irFactory.buildField {
      name = Name.identifier("_bufferedSymbolsFileSink")
      type = rawSinkClass.defaultType
      isFinal = false
      isStatic = true
    }.apply {
      this.initializer = DeclarationIrBuilder(pluginContext, firstFile.symbol).run {
        irExprBody(irCall(bufferedFunc).apply {
          extensionReceiver = irCall(sinkFunc).apply {
            dispatchReceiver = irCall(systemFileSystem.owner.getter!!)
            putValueArgument(
              0,
              irCall(pathConstructionFunc).apply {
                putValueArgument(0, irGetField(null, bufferedSymbolsFileName))
              })
          }
        })
      }
    }
    firstFile.declarations.add(bufferedSymbolsFileSink)
    bufferedSymbolsFileSink.parent = firstFile

    val methodMap = mutableMapOf<String, IrFunction>()
    val methodIdMap = mutableMapOf<String, Int>()
    var currMethodId = 0
    moduleFragment.files.forEach { file ->
      file.transform(object : IrElementTransformerVoidWithContext() {
        override fun visitFunctionNew(declaration: IrFunction): IrStatement {
          methodMap[declaration.kotlinFqName.asString()] = declaration
          methodIdMap[declaration.kotlinFqName.asString()] = currMethodId++
          // do not transform at all
          // we just use a transformer because it correctly descends recursively
          return super.visitFunctionNew(declaration)
        }
      }, null)
    }

    fun buildEnterFunction(): IrFunction {
      val timeSourceMonotonicClass: IrClassSymbol =
        pluginContext.referenceClass(ClassId.fromString("kotlin/time/TimeSource.Monotonic"))!!

      /* val funMarkNowViaClass = classMonotonic.functions.find { it.owner.name.asString() == "markNow" }!! */

      val funMarkNow =
        pluginContext.referenceFunctions(
          CallableId(
            FqName("kotlin.time"),
            FqName("TimeSource.Monotonic"),
            Name.identifier("markNow")
          )
        ).single()

      // assertion: funMarkNowViaClass == funMarkNow

      return pluginContext.irFactory.buildFun {
        name = Name.identifier("_enter_method")
        returnType = timeMarkClass.defaultType
      }.apply {
        addValueParameter {
          /*
      name = Name.identifier("method")
      type = pluginContext.irBuiltIns.stringType
      */
          name = Name.identifier("methodId")
          type = pluginContext.irBuiltIns.intType
        }

        body = DeclarationIrBuilder(
          pluginContext,
          symbol,
          startOffset,
          endOffset
        ).irBlockBody {
          if (STRINGBUILDER_MODE) {
            +irCall(stringBuilderAppendStringFunc).apply {
              dispatchReceiver = irGetField(null, stringBuilder)
              putValueArgument(0, irString(">;"))
            }
            +irCall(stringBuilderAppendIntFunc).apply {
              dispatchReceiver = irGetField(null, stringBuilder)
              putValueArgument(0, irGet(valueParameters[0]))
            }
            +irCall(stringBuilderAppendStringFunc).apply {
              dispatchReceiver = irGetField(null, stringBuilder)
              putValueArgument(0, irString("\n"))
            }
          } else {
            +irCall(writeStringFunc).apply {
              extensionReceiver = irGetField(null, bufferedTraceFileSink)
              putValueArgument(0, irConcat().apply {
                addArgument(irString(">;"))
                addArgument(irGet(valueParameters[0]))
                addArgument(irString("\n"))
              })
            }
          }
          +irReturn(irCall(funMarkNow).also { call ->
            call.dispatchReceiver = irGetObject(timeSourceMonotonicClass)
          })
        }
      }
    }

    val enterFunc = buildEnterFunction()
    firstFile.declarations.add(enterFunc)
    enterFunc.parent = firstFile

    fun buildGeneralExitFunction(): IrFunction {
      val funElapsedNow =
        pluginContext.referenceFunctions(
          CallableId(
            FqName("kotlin.time"),
            FqName("TimeMark"),
            Name.identifier("elapsedNow")
          )
        ).single()

      return pluginContext.irFactory.buildFun {
        name = Name.identifier("_exit_method")
        returnType = pluginContext.irBuiltIns.unitType
      }.apply {
        addValueParameter {
          /*
          name = Name.identifier("method")
          type = pluginContext.irBuiltIns.stringType */
          name = Name.identifier("methodId")
          type = pluginContext.irBuiltIns.intType
        }
        addValueParameter {
          name = Name.identifier("startTime")
          type = timeMarkClass.defaultType
        } /*
                addValueParameter {
                    name = Name.identifier("result")
                    type = pluginContext.irBuiltIns.anyNType
                } */

        body = DeclarationIrBuilder(pluginContext, symbol, startOffset, endOffset).irBlockBody {
          // Duration
          val elapsedDuration = irTemporary(irCall(funElapsedNow).apply {
            dispatchReceiver = irGet(valueParameters[1])
          })
          val elapsedMicrosProp: IrProperty =
            elapsedDuration.type.getClass()!!.properties.single { it.name.asString() == "inWholeMicroseconds" }

          val elapsedMicros = irTemporary(irCall(elapsedMicrosProp.getter!!).apply {
            dispatchReceiver = irGet(elapsedDuration)
          })

          if (STRINGBUILDER_MODE) {
            +irCall(stringBuilderAppendStringFunc).apply {
              dispatchReceiver = irGetField(null, stringBuilder)
              putValueArgument(0, irString("<;"))
            }
            +irCall(stringBuilderAppendIntFunc).apply {
              dispatchReceiver = irGetField(null, stringBuilder)
              putValueArgument(0, irGet(valueParameters[0]))
            }
            +irCall(stringBuilderAppendStringFunc).apply {
              dispatchReceiver = irGetField(null, stringBuilder)
              putValueArgument(0, irString(";"))
            }
            +irCall(stringBuilderAppendLongFunc).apply {
              dispatchReceiver = irGetField(null, stringBuilder)
              putValueArgument(0, irGet(elapsedMicros))
            }
            +irCall(stringBuilderAppendStringFunc).apply {
              dispatchReceiver = irGetField(null, stringBuilder)
              putValueArgument(0, irString("\n"))
            }
          } else {
            +irCall(writeStringFunc).apply {
              extensionReceiver = irGetField(null, bufferedTraceFileSink)
              putValueArgument(0, irConcat().apply {
                addArgument(irString("<;"))
                addArgument(irGet(valueParameters[0]))
                addArgument(irString(";"))
                addArgument(irGet(elapsedMicros))
                addArgument(irString("\n"))
              })
            }
          }
        }
      }
    }

    val exitFunc = buildGeneralExitFunction()
    firstFile.declarations.add(exitFunc)
    exitFunc.parent = firstFile

    fun buildMainExitFunction(): IrSimpleFunction {
      fun IrBlockBodyBuilder.flushTraceFile() {
        +irCall(flushFunc).apply {
          dispatchReceiver = irGetField(null, bufferedTraceFileSink)
        }
      }

      fun IrBlockBodyBuilder.writeAndFlushSymbolsFile() {
        +irCall(writeStringFunc).apply {
          extensionReceiver = irGetField(null, bufferedSymbolsFileSink)
          putValueArgument(0, irString("{ " + methodIdMap.map { (name, id) -> id to name }
            .sortedBy { (id, _) -> id }
            .joinToString(",\n") { (id, name) -> "\"$id\": \"$name\"" } + " }"))
        }
        +irCall(flushFunc).apply {
          dispatchReceiver = irGetField(null, bufferedSymbolsFileSink)
        }
      }

      fun IrBlockBodyBuilder.printFileNamesToStdout() {
        +irCall(printlnFunc).apply {
          putValueArgument(0, irGetField(null, bufferedTraceFileName))
        }
        +irCall(printlnFunc).apply {
          putValueArgument(
            0, irGetField(null, bufferedSymbolsFileName)
          )
        }
      }

      return pluginContext.irFactory.buildFun {
        name = Name.identifier("_exit_main")
        returnType = pluginContext.irBuiltIns.unitType
      }.apply {
        addValueParameter {
          name = Name.identifier("startTime")
          type = timeMarkClass.defaultType
        } /*
                addValueParameter {
                    name = Name.identifier("result")
                    type = pluginContext.irBuiltIns.anyNType
                } */

        body = DeclarationIrBuilder(pluginContext, symbol, startOffset, endOffset).irBlockBody {
          flushTraceFile()

          +irCall(exitFunc).apply {
            val mainName = methodMap.keys.single {
              it == "main" || it.endsWith(".main") // also consider main methods in packages
            }

            putValueArgument(
              0,
              methodIdMap[mainName]!!.toIrConst(pluginContext.irBuiltIns.intType)
            )
            putValueArgument(1, irGet(valueParameters[0]))
          }

          if (STRINGBUILDER_MODE) {
            +irCall(writeStringFunc).apply {
              extensionReceiver = irGetField(null, bufferedTraceFileSink)
              putValueArgument(0, irCall(toStringFunc).apply {
                extensionReceiver = irGetField(null, stringBuilder)
              })
            }
          }

          writeAndFlushSymbolsFile()

          flushTraceFile()

          printFileNamesToStdout()
        }
      }
    }

    val exitMainFunc = buildMainExitFunction()
    firstFile.declarations.add(exitMainFunc)
    exitMainFunc.parent = firstFile

    fun buildBodyWithMeasureCode(func: IrFunction): IrBody {

      println("# Wrapping body of ${func.name} (origin: ${func.origin})")
      return DeclarationIrBuilder(pluginContext, func.symbol).irBlockBody {
        // no +needed on irTemporary as it is automatically added to the builder
        val startTime = irTemporary(irCall(enterFunc).apply {
          putValueArgument(
            0,
            methodIdMap[func.kotlinFqName.asString()]!!.toIrConst(pluginContext.irBuiltIns.intType)
          )
        })

        val tryBlock: IrExpression = irBlock(resultType = func.returnType) {
          for (statement in func.body?.statements ?: listOf()) +statement
        }

        +irTry(
          tryBlock.type,
          tryBlock,
          listOf(),
          if (func.name.asString() == "main") irCall(exitMainFunc).apply {
            putValueArgument(0, irGet(startTime))
          } else irCall(exitFunc).apply {
            putValueArgument(
              0,
              methodIdMap[func.kotlinFqName.asString()]!!.toIrConst(pluginContext.irBuiltIns.intType)
            )
            putValueArgument(1, irGet(startTime))
          }
        )
      }
    }

    // IrElementVisitor / IrElementVisitorVoid
    // IrElementTransformer / IrElementTransformerVoid / IrElementTransformerVoidWithContext
    // IrElementTransformerVoidWithContext().visitfile(file, null)

    moduleFragment.files.forEach { file ->
      file.transform(object : IrElementTransformerVoidWithContext() {
        override fun visitFunctionNew(declaration: IrFunction): IrStatement {
          val body = declaration.body
          if (declaration.name.asString() == "_enter_method" ||
            declaration.name.asString() == "_exit_method" ||
            declaration.name.asString() == "_exit_main" ||
            body == null ||
            declaration.origin == ADAPTER_FOR_CALLABLE_REFERENCE ||
            declaration.fqNameWhenAvailable?.asString()?.contains("<init>") != false ||
            declaration.fqNameWhenAvailable?.asString()?.contains("<anonymous>") != false
          ) {
            // do not further transform this method, e.g., its statements are not transformed
            println("# Do not wrap body of ${declaration.name} (${declaration.fqNameWhenAvailable?.asString()}):\n${declaration.dump()}")
            return declaration
          }
          declaration.body = buildBodyWithMeasureCode(declaration)

          return super.visitFunctionNew(declaration)
        }
      }, null)
      println("---${file.name}---")
      println(file.dump())
    }
  }
}