package org.autotarget.util

import com.squareup.javapoet.ClassName
import org.autotarget.annotation.TargetParameter
import javax.lang.model.element.Element
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror

object ProcessorUtil {

    fun getValueType(element: Element): TypeMirror? {
        try {
            element.getAnnotation(TargetParameter::class.java).type
        } catch (mte: MirroredTypeException) {
            return mte.typeMirror
        }
        return null
    }

    fun classInject(): ClassName {
        return ClassName.get("javax.inject", "Inject")
    }

    fun classSingleton(): ClassName {
        return ClassName.get("javax.inject", "Singleton")
    }
}