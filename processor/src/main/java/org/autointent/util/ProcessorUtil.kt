package org.autointent.util

import org.autointent.annotation.IntentParameter
import javax.lang.model.element.Element
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror

class ProcessorUtil {

    companion object {

        fun getType(className: String, intentParameterMap: Map<String, List<Element>>): TypeMirror? {
            try {
                intentParameterMap[className]!![0].getAnnotation(IntentParameter::class.java).valueType
            } catch (mte: MirroredTypeException) {
                return mte.typeMirror
            }
            return null
        }

    }
}