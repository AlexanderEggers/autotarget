package autotarget

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
                    .addAnnotation(classNullable)
                    .build()

            builder.addParameter(parameter)
                    .addStatement("parameterList.add(new ${classParameterProvider}(\"$valueKey\", $valueName))")
        }

        return paramCount
    }

    val classNullable: ClassName = ClassName.get("android.support.annotation", "Nullable")
    val classNonNull: ClassName = ClassName.get("android.support.annotation", "NonNull")

    val classActivityTarget: ClassName = ClassName.get("autotarget.service", "ActivityTarget")
    val classFragmentTarget: ClassName = ClassName.get("autotarget.service", "FragmentTarget")
    val classParameterProvider: ClassName = ClassName.get("autotarget.service", "ParameterProvider")

    val classList: ClassName = ClassName.get("java.util", "List")
    val classArrayList: ClassName = ClassName.get("java.util", "ArrayList")

}