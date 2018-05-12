package autotarget.util

import autotarget.annotation.TargetParameter
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import javax.lang.model.element.Element
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException

object ProcessorUtil {

    private fun getValueType(element: Element): DeclaredType? {
        try {
            element.getAnnotation(TargetParameter::class.java).type
        } catch (mte: MirroredTypeException) {
            return mte.typeMirror as DeclaredType
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

            val parameter = ParameterSpec.builder(ClassName.get(getValueType(it)), valueName)
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
        return ClassName.get("autotarget.service", "ParameterProvider")
    }

    fun classList(): ClassName {
        return ClassName.get("java.util", "List")
    }

    fun classArrayList(): ClassName {
        return ClassName.get("java.util", "ArrayList")
    }
}