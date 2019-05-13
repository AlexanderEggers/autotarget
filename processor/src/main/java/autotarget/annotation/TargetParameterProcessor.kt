package autotarget.annotation

import autotarget.MainProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.tools.Diagnostic

class TargetParameterProcessor {

    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment): HashMap<String, Element> {
        val targetParameterMap = HashMap<String, Element>()

        for (it in roundEnv.getElementsAnnotatedWith(TargetParameter::class.java)) {
            if (!it.kind.isClass) {
                mainProcessor.messager.printMessage(Diagnostic.Kind.ERROR,
                        "Can only be applied to a class. Error for ${it.simpleName}")
                continue
            }

            val className = it.simpleName.toString()
            targetParameterMap[className] = it
        }

        return targetParameterMap
    }
}
