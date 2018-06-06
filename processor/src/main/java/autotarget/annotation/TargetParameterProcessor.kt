package autotarget.annotation

import autotarget.MainProcessor
import javax.annotation.processing.RoundEnvironment
import javax.tools.Diagnostic

class TargetParameterProcessor {

    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        for (it in roundEnv.getElementsAnnotatedWith(TargetParameter::class.java)) {
            if (!it.kind.isClass) {
                mainProcessor.messager!!.printMessage(Diagnostic.Kind.ERROR,
                        "Can only be applied to a class. Error for object: ${it.simpleName}")
                continue
            }

            val className = it.simpleName.toString()
            val elements = mainProcessor.targetParameterMap!![className] ?: ArrayList()
            elements.add(it)
            mainProcessor.targetParameterMap!![className] = elements
        }
    }
}
