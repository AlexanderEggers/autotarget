package org.autotarget.util

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
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

    fun populateParamListBody(list: ArrayList<Element>, builder: MethodSpec.Builder, initParamCount: Int): Int {
        var paramCount = initParamCount

        list.forEach {
            var valueName = it.getAnnotation(TargetParameter::class.java).name
            val valueKey = it.getAnnotation(TargetParameter::class.java).key

            if (valueName == "unspecified") {
                valueName = "param$paramCount"
                paramCount++
            }

            val parameter = ParameterSpec.builder(ClassName.get(ProcessorUtil.getValueType(it)), valueName)
                    .addAnnotation(classNullable())
                    .build()

            builder.addParameter(parameter)
                    .addStatement("parameterList.add(new ${classParameterProvider()}(\"$valueKey\", $valueName))")
        }

        return paramCount
    }

    fun classNullable(): ClassName {
        return ClassName.get("android.support.annotation", "Nullable")
    }

    fun classNonNull(): ClassName {
        return ClassName.get("android.support.annotation", "NonNull")
    }

    fun classParameterProvider(): ClassName {
        return ClassName.get("org.autotarget.service", "ParameterProvider")
    }

    fun classList(): ClassName {
        return ClassName.get("java.util", "List")
    }

    fun classArrayList(): ClassName {
        return ClassName.get("java.util", "ArrayList")
    }
}