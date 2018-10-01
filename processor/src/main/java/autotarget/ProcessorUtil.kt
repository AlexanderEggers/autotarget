package autotarget

import autotarget.annotation.TargetParameterItem
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror

object ProcessorUtil {

    private fun getValueType(element: TargetParameterItem): TypeMirror? {
        try {
            element.type
        } catch (mte: MirroredTypeException) {
            return mte.typeMirror
        }
        return null
    }

    fun populateParamListBody(list: ArrayList<TargetParameterItem>, builder: MethodSpec.Builder, initParamCount: Int): Int {
        var paramCount = initParamCount

        list.forEach {
            var valueName = it.name
            val valueKey = it.key
            val valueType = ClassName.get(getValueType(it))

            if (valueName == "unspecified") {
                valueName = "param$paramCount"
                paramCount++
            }

            val parameter = ParameterSpec.builder(valueType, valueName)
                    .addAnnotation(classNullable)
                    .build()

            builder.addParameter(parameter)

            when (valueType) {
                classBundle -> builder.addStatement("parameterList.add(new $classBundleParameterProvider(\"$valueKey\", $valueName))")
                classParcelable -> builder.addStatement("parameterList.add(new $classParcelableParameterProvider(\"$valueKey\", $valueName))")
                else -> builder.addStatement("parameterList.add(new $classValueParameterProvider(\"$valueKey\", $valueName))")
            }
        }

        return paramCount
    }

    val classNullable: ClassName = ClassName.get("android.support.annotation", "Nullable")
    val classNonNull: ClassName = ClassName.get("android.support.annotation", "NonNull")

    val classActivityTarget: ClassName = ClassName.get("autotarget.service", "ActivityTarget")
    val classFragmentTarget: ClassName = ClassName.get("autotarget.service", "FragmentTarget")

    val classParameterProvider: ClassName = ClassName.get("autotarget.service", "ParameterProvider")
    val classBundleParameterProvider: ClassName = ClassName.get("autotarget.service", "BundleParameterProvider")
    val classParcelableParameterProvider: ClassName = ClassName.get("autotarget.service", "ParcelableParameterProvider")
    val classValueParameterProvider: ClassName = ClassName.get("autotarget.service", "ValueParameterProvider")

    val classBundle: ClassName = ClassName.get("android.os", "Bundle")
    val classParcelable: ClassName = ClassName.get("android.os", "Parcelable")

    val classList: ClassName = ClassName.get("java.util", "List")
    val classArrayList: ClassName = ClassName.get("java.util", "ArrayList")
}