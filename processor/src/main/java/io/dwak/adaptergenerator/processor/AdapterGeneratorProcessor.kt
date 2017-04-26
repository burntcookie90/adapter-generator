package io.dwak.adaptergenerator.processor

import io.dwak.adaptergenerator.annotation.AdapterGenerator
import io.dwak.adaptergenerator.processor.binding.GeneratorBindingClass
import io.dwak.adaptergenerator.processor.extension.*
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

open class AdapterGeneratorProcessor : AbstractProcessor() {
  private lateinit var filer: Filer
  private lateinit var messager: Messager
  private lateinit var elementUtils: Elements

  override fun init(processingEnv: ProcessingEnvironment) {
    super.init(processingEnv)
    this.filer = processingEnv.filer
    this.messager = processingEnv.messager
    this.elementUtils = processingEnv.elementUtils
  }

  override fun getSupportedAnnotationTypes() = mutableSetOf(AdapterGenerator::class.java.canonicalName)

  override fun getSupportedSourceVersion() = SourceVersion.latestSupported()

  override fun process(annotations: MutableSet<out TypeElement>,
                       roundEnv: RoundEnvironment): Boolean {
    val generatorTargetClassMap = hashMapOf<TypeElement, GeneratorBindingClass>()
    val erasedTargetNames = mutableSetOf<String>()

    if (annotations.isNotEmpty()) {
      annotations.flatMap { roundEnv.getElementsAnnotatedWith(it) }
          .filter { it.hasAnnotationWithName(AdapterGenerator::class.java.simpleName) }
          .forEach {
            try {
              val generatorClass = getOrCreateGenerator(generatorTargetClassMap,
                  it as TypeElement,
                  erasedTargetNames)
              generatorClass.createAndAddBinding(it)
            } catch(e: Exception) {
              messager.error(it, "$e")
            }
          }
    }
    messager.note("$generatorTargetClassMap")
    generatorTargetClassMap.values.forEach {
      try {
        it.writeToFiler(filer)
      } catch (e: IOException) {
        messager.error(message = e.message)
      }
    }

    return true
  }

  private fun getOrCreateGenerator(targetClassMap: MutableMap<TypeElement, GeneratorBindingClass>,
                                   enclosingElement: TypeElement,
                                   erasedTargetNames: MutableSet<String>)
      : GeneratorBindingClass {
    var generatorBindingClass = targetClassMap[enclosingElement]
    if (generatorBindingClass == null) {
      val targetClass = enclosingElement.simpleName.toString()
      val classPackage = enclosingElement.packageName(elementUtils)
      val className = enclosingElement.className(classPackage) + GeneratorBindingClass.CLASS_SUFFIX
      generatorBindingClass = GeneratorBindingClass(classPackage,
          className,
          targetClass,
          processingEnv)
      targetClassMap.put(enclosingElement, generatorBindingClass)
      erasedTargetNames.add(enclosingElement.toString())
    }

    return generatorBindingClass
  }
}
