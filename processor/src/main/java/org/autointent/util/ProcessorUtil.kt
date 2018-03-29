package org.autointent.util

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeName
import org.autointent.annotation.IntentParameter
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
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

        fun createContextProviderField(): FieldSpec {
            val contextProviderClass: TypeName = ClassName.get("org.autointent.generated", "ContextProvider")
            val contextInjectorClass: TypeName = ClassName.get("org.autointent.generated", "ContextInjector")

            return FieldSpec.builder(contextProviderClass, "contextProvider")
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .initializer("$contextInjectorClass.getInstance()")
                    .build()
        }
    }
}