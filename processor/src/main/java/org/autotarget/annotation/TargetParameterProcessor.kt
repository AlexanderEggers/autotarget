package org.autotarget.annotation

import org.autotarget.MainProcessor
import org.autotarget.util.AnnotationProcessor
import javax.annotation.processing.RoundEnvironment

class TargetParameterProcessor : AnnotationProcessor {

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(TargetParameter::class.java)
                .forEach {
                    val className = it.simpleName.toString()
                    var elements = mainProcessor.targetParameterMap!![className]

                    if (elements == null) {
                        elements = ArrayList()
                    }

                    elements.add(it)
                    mainProcessor.targetParameterMap!![className] = elements
                }
    }
}
