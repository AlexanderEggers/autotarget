package autotarget

import autotarget.annotation.TargetParameter
import autotarget.annotation.TargetParameterItem
import com.squareup.javapoet.*
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object ProcessorUtil {

    private fun getValueType(element: TargetParameterItem): TypeMirror? {
        try {
            element.type
        } catch (mte: MirroredTypeException) {
            return mte.typeMirror
        }
        return null
    }

    private fun isParcelableObject(processingEnv: ProcessingEnvironment,
                                   typeMirror: TypeMirror?): Boolean {

        return processingEnv.typeUtils.isAssignable(typeMirror,
                processingEnv.elementUtils.getTypeElement(classParcelable).asType())
    }

    private fun isSerializableObject(processingEnv: ProcessingEnvironment,
                                     typeMirror: TypeMirror?): Boolean {

        return processingEnv.typeUtils.isAssignable(typeMirror,
                processingEnv.elementUtils.getTypeElement(classSerializable).asType())
    }

    fun populateParamListBody(processingEnv: ProcessingEnvironment,
                              list: List<TargetParameterItem>,
                              builder: MethodSpec.Builder): Int {

        var paramCount = 0

        loop@ for (it in list) {
            var valueName = it.name
            val valueKey = it.key
            val typeMirror = getValueType(it)
            val valueType = ClassName.get(typeMirror)

            if (valueName.isBlank()) {
                valueName = "param$paramCount"
                paramCount++
            }

            when {
                valueType == classBundle -> {
                    builder.addStatement("parameterList.add(new $classBundleParameterProvider(\"$valueKey\", $valueName))")
                }
                isParcelableObject(processingEnv, typeMirror) -> {
                    builder.addStatement("parameterList.add(new $classParcelableParameterProvider(\"$valueKey\", $valueName))")
                }
                isSerializableObject(processingEnv, typeMirror) -> {
                    builder.addStatement("parameterList.add(new $classSerializableParameterProvider(\"$valueKey\", $valueName))")
                }
                else -> {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "The type of " +
                            "the parameter ${it.name} is not supported. You can use Bundle, " +
                            "Parcelable or Serializable for target parameter.")
                    continue@loop
                }
            }

            val parameterBuilder = ParameterSpec.builder(valueType, valueName)
            if (typeMirror !is PrimitiveType && !it.required) {
                parameterBuilder.addAnnotation(classNullable)
            } else if (typeMirror !is PrimitiveType && it.required) {
                parameterBuilder.addAnnotation(classNonNull)
            }
            builder.addParameter(parameterBuilder.build())
        }

        return paramCount
    }

    fun populateBundleModel(processingEnv: ProcessingEnvironment,
                            list: List<TargetParameterItem>,
                            builder: TypeSpec.Builder): Int {

        var paramCount = 0

        val constructorBuilder = MethodSpec.constructorBuilder()
                .addParameter(ParameterSpec.builder(classBundle, "bundle")
                        .addAnnotation(classNonNull)
                        .build()
                )
                .addStatement("if(bundle == null) throw new $classRuntimeException(\"Bundle cannot be null.\")")
                .addCode("\n")

        loop@ for (it in list) {
            var valueName = it.name
            val typeMirror = getValueType(it)
            val valueType = ClassName.get(typeMirror)

            if (valueName.isBlank()) {
                valueName = "param$paramCount"
                paramCount++
            }

            val fieldBuilder = FieldSpec.builder(valueType, valueName, Modifier.PRIVATE)
            builder.addField(fieldBuilder.build())

            when {
                valueType == classBundle -> constructorBuilder.addStatement("$valueName = bundle.getBundle(\"${it.key}\")")
                valueType == ClassName.INT -> constructorBuilder.addStatement("$valueName = bundle.getInt(\"${it.key}\")")
                valueType == ClassName.CHAR -> constructorBuilder.addStatement("$valueName = bundle.getChar(\"${it.key}\")")
                valueType == ClassName.BYTE -> constructorBuilder.addStatement("$valueName = bundle.getByte(\"${it.key}\")")
                valueType == ClassName.BOOLEAN -> constructorBuilder.addStatement("$valueName = bundle.getBoolean(\"${it.key}\")")
                valueType == ClassName.LONG -> constructorBuilder.addStatement("$valueName = bundle.getLong(\"${it.key}\")")
                valueType == ClassName.DOUBLE -> constructorBuilder.addStatement("$valueName = bundle.getDouble(\"${it.key}\")")
                valueType == ClassName.FLOAT -> constructorBuilder.addStatement("$valueName = bundle.getFloat(\"${it.key}\")")
                valueType == classString -> constructorBuilder.addStatement("$valueName = bundle.getString(\"${it.key}\")")
                isParcelableObject(processingEnv, typeMirror) -> constructorBuilder.addStatement("$valueName = bundle.getParcelable(\"${it.key}\")")
                isSerializableObject(processingEnv, typeMirror) -> constructorBuilder.addStatement("$valueName = ($valueType) bundle.getSerializable(\"${it.key}\")")
                else -> {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "The type of " +
                            "the parameter ${it.name} is not supported. You can use Bundle, " +
                            "Parcelable or Serializable for target parameter.")
                    continue@loop
                }
            }

            val valueGetter = MethodSpec.methodBuilder("get${valueName.substring(0, 1).toUpperCase(Locale.ROOT) + valueName.substring(1)}")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(valueType)
                    .addStatement("return $valueName")

            if (typeMirror !is PrimitiveType && !it.required) {
                valueGetter.addAnnotation(classNullable)
            } else if (typeMirror !is PrimitiveType && it.required) {
                valueGetter.addAnnotation(classNonNull)
            }

            builder.addMethod(valueGetter.build())
        }

        builder.addMethod(constructorBuilder.build())

        return paramCount
    }

    fun createTargetParameterMap(annotationElement: Element): HashMap<String, ArrayList<TargetParameterItem>> {
        val parameterMap = HashMap<String, ArrayList<TargetParameterItem>>()
        addRequiredParameters(annotationElement, parameterMap)
        addGroupSpecificTargetParameters(annotationElement, parameterMap)
        return parameterMap
    }

    private fun addRequiredParameters(annotationElement: Element,
                                      parameterMap: HashMap<String, ArrayList<TargetParameterItem>>) {

        val targetParameter = annotationElement.getAnnotation(TargetParameter::class.java)
        parameterMap[libraryDefaultGroupKey] = ArrayList()

        targetParameter?.value?.forEach {
            if (it.required) addTargetParameterToGroup(parameterMap, it, libraryDefaultGroupKey)
        }
    }

    private fun addGroupSpecificTargetParameters(annotationElement: Element,
                                                 parameterMap: HashMap<String, ArrayList<TargetParameterItem>>) {

        val targetParameter = annotationElement.getAnnotation(TargetParameter::class.java)
        targetParameter?.value?.forEach {
            if (!it.required) {
                it.group.forEach { group ->
                    addTargetParameterToGroup(parameterMap, it, group)
                }

                //Ensures that optional parameters are at least at added to an optional group
                addTargetParameterToGroup(parameterMap, it, libraryOptionalGroupKey)
            }
        }
    }

    private fun addTargetParameterToGroup(parameterMap: HashMap<String, ArrayList<TargetParameterItem>>,
                                          item: TargetParameterItem,
                                          groupId: String) {

        val list = parameterMap[groupId] ?: run {
            val defaultList = parameterMap[libraryDefaultGroupKey] ?: ArrayList()
            ArrayList(defaultList)
        }
        list.add(item)
        parameterMap[groupId] = list
    }

    const val libraryGeneratedPackageName = "autotarget.generated"
    const val libraryDefaultGroupKey = "__&&default&&__" //ensures that the default group key stays unique
    const val libraryOptionalGroupKey = "__&&optional&&__"

    val classString: ClassName = ClassName.get("java.lang", "String")
    val classRuntimeException: ClassName = ClassName.get("java.lang", "RuntimeException")

    const val classSerializable = "java.io.Serializable"

    val classList: ClassName = ClassName.get("java.util", "List")
    val classArrayList: ClassName = ClassName.get("java.util", "ArrayList")

    val classNullable: ClassName = ClassName.get("androidx.annotation", "Nullable")
    val classNonNull: ClassName = ClassName.get("androidx.annotation", "NonNull")

    val classBundle: ClassName = ClassName.get("android.os", "Bundle")
    const val classParcelable = "android.os.Parcelable"

    const val libraryParameterPackageName = "autotarget.parameter"
    val classBundleParameterProvider: ClassName = ClassName.get(libraryParameterPackageName, "BundleParameterProvider")
    val classParcelableParameterProvider: ClassName = ClassName.get(libraryParameterPackageName, "ParcelableParameterProvider")
    val classSerializableParameterProvider: ClassName = ClassName.get(libraryParameterPackageName, "SerializableParameterProvider")
    val classParameterProvider: ClassName = ClassName.get(libraryParameterPackageName, "ParameterProvider")
    val listOfParameterProvider = ParameterizedTypeName.get(classList, classParameterProvider)

    const val libraryTargetPackageName = "autotarget.target"
    val classActivityTarget: ClassName = ClassName.get(libraryTargetPackageName, "ActivityTarget")
    val classFragmentTarget: ClassName = ClassName.get(libraryTargetPackageName, "FragmentTarget")
}