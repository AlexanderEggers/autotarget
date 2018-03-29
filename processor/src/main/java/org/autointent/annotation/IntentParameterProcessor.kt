package org.autointent.annotation

import org.autointent.MainProcessor
import org.autointent.util.AnnotationProcessor
import javax.annotation.processing.RoundEnvironment

class IntentParameterProcessor : AnnotationProcessor {

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(IntentParameter::class.java)
                .forEach {
                    val className = it.simpleName.toString()
                    var elements = mainProcessor.intentParameterMap!![className]

                    if (elements == null) {
                        elements = ArrayList()
                    }

                    elements.add(it)
                    mainProcessor.intentParameterMap!![className] = elements
                }
    }
}
