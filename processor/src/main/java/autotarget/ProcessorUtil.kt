package autotarget

import autotarget.annotation.TargetParameterItem
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.PrimitiveType
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

    fun populateParamListBody(list: ArrayList<TargetParameterItem>, builder: MethodSpec.Builder): Int {
        var paramCount = 0

        list.forEach {
            var valueName = it.name
            val valueKey = it.key
            val typeMirror = getValueType(it)
            val valueType = ClassName.get(typeMirror)

            if (valueName == "unspecified") {
                valueName = "param$paramCount"
                paramCount++
            }

            val parameterBuilder = ParameterSpec.builder(valueType, valueName)
            if(typeMirror !is PrimitiveType) {
                parameterBuilder.addAnnotation(classNullable)
            }

            builder.addParameter(parameterBuilder.build())

            when (valueType) {
                classBundle -> builder.addStatement("parameterList.add(new $classBundleParameterProvider(\"$valueKey\", $valueName))")
                classParcelable -> builder.addStatement("parameterList.add(new $classParcelableParameterProvider(\"$valueKey\", $valueName))")
                else -> builder.addStatement("parameterList.add(new $classValueParameterProvider(\"$valueKey\", $valueName))")
            }
        }

        return paramCount
    }

    val classNullable: ClassName = ClassName.get("androidx.annotation", "Nullable")
    val classNonNull: ClassName = ClassName.get("androidx.annotation", "NonNull")

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