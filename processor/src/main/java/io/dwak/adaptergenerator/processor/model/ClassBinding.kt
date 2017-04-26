package io.dwak.adaptergenerator.processor.model

import io.dwak.adaptergenerator.annotation.AdapterGenerator
import io.dwak.adaptergenerator.annotation.BindViewHolder
import io.dwak.adaptergenerator.processor.extension.hasAnnotationWithName
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror

class ClassBinding(element: Element) {
  val name: String = element.simpleName.toString()
  val type: TypeMirror = element.asType()
  val kind: ElementKind = element.kind
  val layoutResId: Int
  val model: TypeMirror?
  val enclosedBindMethod: FieldBinding

  init {
    val instance = element.getAnnotation(AdapterGenerator::class.java)
    layoutResId = instance.layoutResId
    model = getModel(instance)

    enclosedBindMethod = element.enclosedElements
        .filter { it.hasAnnotationWithName(BindViewHolder::class.java.simpleName) }
        .take(1)
        .map { FieldBinding(it) }
        .first()
  }

  companion object {
    fun getModel(annotation: AdapterGenerator): TypeMirror? {
      try {
        annotation.model// this should throw
      } catch (mte: MirroredTypeException) {
        return mte.typeMirror
      }
      return null
    }
  }

}