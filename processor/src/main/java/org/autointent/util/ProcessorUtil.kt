package org.autointent.util

import org.autointent.annotation.IntentParameter
import javax.lang.model.element.Element
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror

class ProcessorUtil {

    companion object {

        fun getValueType(element: Element): TypeMirror? {
            try {
                element.getAnnotation(IntentParameter::class.java).valueType
            } catch (mte: MirroredTypeException) {
                return mte.typeMirror
            }
            return null
        }
    }
}