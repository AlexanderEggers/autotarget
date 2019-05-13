package autotarget

import autotarget.annotation.TargetParameterItem
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier
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
            if (typeMirror !is PrimitiveType) {
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

    fun populateBundleModel(list: ArrayList<TargetParameterItem>, builder: TypeSpec.Builder): Int {
        var paramCount = 0

        val constructorBuilder = MethodSpec.constructorBuilder()
                .addParameter(ParameterSpec.builder(classBundle, "bundle")
                        .addAnnotation(classNullable)
                        .build()
                )
                .addStatement("if(bundle == null) return")
                .addCode("\n")

        list.forEach {
            var valueName = it.name
            val typeMirror = getValueType(it)
            val valueType = ClassName.get(typeMirror)

            if (valueName == "unspecified") {
                valueName = "param$paramCount"
                paramCount++
            }

            val fieldBuilder = FieldSpec.builder(valueType, valueName, Modifier.PRIVATE)
            builder.addField(fieldBuilder.build())

            when (valueType) {
                classBundle -> constructorBuilder.addStatement("$valueName = bundle.getBundle(\"${it.key}\")")
                classParcelable -> constructorBuilder.addStatement("$valueName = bundle.getParcelable(\"${it.key}\")")
                ClassName.INT -> constructorBuilder.addStatement("$valueName = bundle.getInt(\"${it.key}\")")
                ClassName.CHAR -> constructorBuilder.addStatement("$valueName = bundle.getChar(\"${it.key}\")")
                ClassName.BYTE -> constructorBuilder.addStatement("$valueName = bundle.getByte(\"${it.key}\")")
                ClassName.BOOLEAN -> constructorBuilder.addStatement("$valueName = bundle.getBoolean(\"${it.key}\")")
                ClassName.LONG -> constructorBuilder.addStatement("$valueName = bundle.getLong(\"${it.key}\")")
                ClassName.DOUBLE -> constructorBuilder.addStatement("$valueName = bundle.getDouble(\"${it.key}\")")
                ClassName.FLOAT -> constructorBuilder.addStatement("$valueName = bundle.getFloat(\"${it.key}\")")
                classString -> constructorBuilder.addStatement("$valueName = bundle.getString(\"${it.key}\")")
                else -> constructorBuilder.addStatement("$valueName = ($valueType) bundle.getSerializable(\"${it.key}\")")
            }

            val valueGetter = MethodSpec.methodBuilder("get${valueName.substring(0, 1).toUpperCase() + valueName.substring(1)}")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(valueType)
                    .addStatement("return $valueName")

            if(typeMirror !is PrimitiveType) valueGetter.addAnnotation(classNullable)
            builder.addMethod(valueGetter.build())
        }

        builder.addMethod(constructorBuilder.build())

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
    val classString: ClassName = ClassName.get("java.lang", "String")

    val classList: ClassName = ClassName.get("java.util", "List")
    val classArrayList: ClassName = ClassName.get("java.util", "ArrayList")
}