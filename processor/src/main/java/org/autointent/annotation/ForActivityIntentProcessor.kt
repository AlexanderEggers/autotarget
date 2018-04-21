package org.autointent.annotation

import com.squareup.javapoet.*
import com.squareup.javapoet.ClassName
import org.autointent.MainProcessor
import org.autointent.util.AnnotationProcessor
import org.autointent.util.ProcessorUtil
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ForActivityIntentProcessor : AnnotationProcessor {

    private val classActivityTarget: TypeName = ClassName.get("org.autointent.service", "ActivityTarget")
    private val classParameterProvider = ClassName.get("org.autointent.service", "ParameterProvider")

    private val classNonNull = ClassName.get("android.support.annotation", "NonNull")
    private val classNullable = ClassName.get("android.support.annotation", "Nullable")

    private val classList = ClassName.get("java.util", "List")
    private val classArrayList = ClassName.get("java.util", "ArrayList")

    private val listOfParameterProvider: TypeName = ParameterizedTypeName.get(classList, classParameterProvider)

    private val activitiesWithPackage: HashMap<String, String> = HashMap()
    private var intentParameterMap: HashMap<String, ArrayList<Element>>? = null

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        intentParameterMap = mainProcessor.intentParameterMap

        val fileBuilder = TypeSpec.classBuilder("ActivityService")
                .addModifiers(Modifier.PUBLIC)

        prepareActivityPackageMap(mainProcessor, roundEnv)
        createMethodsForActivities(fileBuilder)

        val file = fileBuilder.build()
        JavaFile.builder("org.autointent.generated", file)
                .build()
                .writeTo(mainProcessor.filer)
    }

    private fun prepareActivityPackageMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(ForActivityIntent::class.java)
                .forEach {
                    if (it.kind != ElementKind.CLASS) {
                        mainProcessor.messager!!.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.")
                        return
                    }

                    val typeElement = it as TypeElement
                    activitiesWithPackage[typeElement.simpleName.toString()] =
                            mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString()
                }
    }

    private fun createMethodsForActivities(fileBuilder: TypeSpec.Builder) {
        activitiesWithPackage.forEach { activityName, packageName ->
            var paramCount = 0
            val activityClass = ClassName.get(packageName, activityName)

            val methodBuilderBase = MethodSpec.methodBuilder("show$activityName")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addAnnotation(classNonNull)
                    .returns(classActivityTarget)
                    .addStatement("$listOfParameterProvider parameterList = new $classArrayList<>()")

            intentParameterMap!![activityName]?.forEach {
                var valueName = it.getAnnotation(IntentParameter::class.java).valueName
                val valueKey = it.getAnnotation(IntentParameter::class.java).valueKey
                val isNonNull = it.getAnnotation(IntentParameter::class.java).isNonNull

                if (valueName == "unspecified") {
                    valueName = "param$paramCount"
                    paramCount++
                }

                val parameter = ParameterSpec.builder(ClassName.get(ProcessorUtil.getValueType(it)), valueName)
                        .addAnnotation(if (isNonNull) classNonNull else classNullable)
                        .build()

                methodBuilderBase.addParameter(parameter)
                        .addStatement("parameterList.add(new $classParameterProvider(\"$valueKey\", $valueName))")
            }

            methodBuilderBase.addStatement("return new $classActivityTarget($activityClass.class, parameterList)")

            fileBuilder.addMethod(methodBuilderBase.build())
        }
    }
}
