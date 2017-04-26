package io.dwak.adaptergenerator.processor.extension

import javax.annotation.processing.ProcessingEnvironment

fun ProcessingEnvironment.getTypeMirror(qualifiedName : String)
        = elementUtils.getTypeElement(qualifiedName).asType()

