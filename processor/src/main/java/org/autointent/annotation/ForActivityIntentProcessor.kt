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
    private var targetParameterMap: HashMap<String, ArrayList<Element>>? = null

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        targetParameterMap = mainProcessor.targetParameterMap

        val fileBuilder = TypeSpec.classBuilder("ActivityTargets")
                .addModifiers(Modifier.PUBLIC)

        prepareActivityPackageMap(mainProcessor, roundEnv)
        createMethodsForActivities(fileBuilder)

        val file = fileBuilder.build()
        JavaFile.builder("org.autointent.generated", file)
                .build()
                .writeTo(mainProcessor.filer)
    }

    private fun prepareActivityPackageMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(ActivityTarget::class.java)
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
            val baseList: ArrayList<Element> = ArrayList()
            val optionalList: ArrayList<Element> = ArrayList()

            val activityClass = ClassName.get(packageName, activityName)

            val methodBuilderBase = MethodSpec.methodBuilder("show$activityName")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addAnnotation(classNonNull)
                    .returns(classActivityTarget)
                    .addStatement("$listOfParameterProvider parameterList = new $classArrayList<>()")
            val methodBuilderWithOptionals = MethodSpec.methodBuilder("show$activityName")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addAnnotation(classNonNull)
                    .returns(classActivityTarget)
                    .addStatement("$listOfParameterProvider parameterList = new $classArrayList<>()")

            targetParameterMap!![activityName]?.forEach {
                val isOptional = it.getAnnotation(TargetParameter::class.java).optional

                if(isOptional) {
                    optionalList.add(it)
                } else {
                    baseList.add(it)
                }
            }

            if(!baseList.isEmpty() || optionalList.isEmpty()) {
                populateMethodBody(baseList, methodBuilderBase, 0)

                methodBuilderBase.addStatement("return new $classActivityTarget($activityClass.class, parameterList)")
                fileBuilder.addMethod(methodBuilderBase.build())
            }

            if(!optionalList.isEmpty()) {
                val paramCountOptional = populateMethodBody(baseList, methodBuilderWithOptionals, 0)
                populateMethodBody(optionalList, methodBuilderWithOptionals, paramCountOptional)

                methodBuilderWithOptionals.addStatement("return new $classActivityTarget($activityClass.class, parameterList)")
                fileBuilder.addMethod(methodBuilderWithOptionals.build())
            }
        }
    }

    private fun populateMethodBody(list: ArrayList<Element>, builder: MethodSpec.Builder, initParamCount: Int): Int {
        var paramCount = initParamCount

        list.forEach {
            var valueName = it.getAnnotation(TargetParameter::class.java).name
            val valueKey = it.getAnnotation(TargetParameter::class.java).key

            if (valueName == "unspecified") {
                valueName = "param$paramCount"
                paramCount++
            }

            val parameter = ParameterSpec.builder(ClassName.get(ProcessorUtil.getValueType(it)), valueName)
                    .addAnnotation(classNullable)
                    .build()

            builder.addParameter(parameter)
                    .addStatement("parameterList.add(new $classParameterProvider(\"$valueKey\", $valueName))")
        }

        return paramCount
    }
}
