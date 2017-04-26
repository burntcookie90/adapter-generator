package io.dwak.adaptergenerator.processor.model

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.TypeMirror

class FieldBinding(element: Element) {
  val name: String = element.simpleName.toString()
  val type: TypeMirror
  val kind: ElementKind = element.kind

  init {
    type = if (kind == ElementKind.FIELD) {
      element.asType()
    } else {
      (element as ExecutableElement).parameters[0].asType()
    }
  }

}