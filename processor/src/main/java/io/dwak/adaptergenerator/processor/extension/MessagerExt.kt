package io.dwak.adaptergenerator.processor.extension

import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.tools.Diagnostic

@Suppress("unused")
fun Messager.error(element: Element? = null, message: String?) {
  printMessage(Diagnostic.Kind.ERROR, message ?: "", element)
}

@Suppress("unused")
fun Messager.note(note: String) {
  printMessage(Diagnostic.Kind.NOTE, note)
}
