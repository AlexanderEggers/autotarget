package org.autointent.annotation

import com.squareup.javapoet.*
import org.autointent.MainProcessor
import org.autointent.util.AnnotationProcessor
import org.autointent.util.ProcessorUtil
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ForActivityProcessor : AnnotationProcessor {

    private val classParameterProvider = ClassName.get("org.autointent.generated", "ParameterProvider")
    private val classActivityIntent = ClassName.get("org.autointent.generated", "ActivityIntent")
    private val classNavigationService = ClassName.get("org.autointent.generated", "NavigationService")

    private val classList = ClassName.get("java.util", "List")
    private val classClass = ClassName.get("java.lang", "Class")
    private val classArrayList = ClassName.get("java.util", "ArrayList")

    private val listOfParameterProvider: TypeName = ParameterizedTypeName.get(classList, classParameterProvider)

    private val activitiesWithPackage: HashMap<String, String> = HashMap()
    private var intentParameterMap: HashMap<String, ArrayList<Element>>? = null

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        intentParameterMap = mainProcessor.intentParameterMap

        val fileBuilder = TypeSpec.classBuilder("ActivityService")
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(classNavigationService, "navigationService")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("new $classNavigationService()")
                        .build())

        prepareActivityPackageMap(mainProcessor, roundEnv)
        createMethodsForActivities(fileBuilder)
        createInitialiseMethod(fileBuilder)

        val file = fileBuilder.build()
        JavaFile.builder("org.autointent.generated", file)
                .build()
                .writeTo(mainProcessor.filer)
    }

    private fun prepareActivityPackageMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(ForActivity::class.java)
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

    private fun createInitialiseMethod(fileBuilder: TypeSpec.Builder) {
        fileBuilder.addMethod(MethodSpec.methodBuilder("prepareIntent")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(classClass, "activityClass")
                .addParameter(listOfParameterProvider, "parameterList")
                .addStatement("return new $classActivityIntent(activityClass, parameterList)")
                .returns(classActivityIntent)
                .build())
    }

    private fun createMethodsForActivities(fileBuilder: TypeSpec.Builder) {
        activitiesWithPackage.forEach { activityName, packageName ->
            var paramCount = 0
            val activityClass = ClassName.get(packageName, activityName)
            val paramList: ArrayList<String> = ArrayList()

            val methodBuilderBase = MethodSpec.methodBuilder("show$activityName")
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("$listOfParameterProvider parameterList = new $classArrayList<>()")
            val methodBuilderOverloadMethodNotEmpty = MethodSpec.methodBuilder("show$activityName")
                    .addModifiers(Modifier.PUBLIC)
            val methodBuilderOverloadMethodEmpty = MethodSpec.methodBuilder("show$activityName")
                    .addModifiers(Modifier.PUBLIC)

            intentParameterMap!![activityName]!!.forEach {
                var valueName = it.getAnnotation(IntentParameter::class.java).valueName
                val valueKey = it.getAnnotation(IntentParameter::class.java).valueKey

                if(valueName == "unspecified") {
                    valueName = "param$paramCount"
                    paramCount++
                }

                methodBuilderBase.addParameter(ClassName.get(ProcessorUtil.getType(it)), valueName)
                        .addStatement("parameterList.add(new $classParameterProvider(\"$valueKey\", $valueName))")
                methodBuilderOverloadMethodNotEmpty.addParameter(ClassName.get(ProcessorUtil.getType(it)), valueName)
                methodBuilderOverloadMethodEmpty.addParameter(ClassName.get(ProcessorUtil.getType(it)), valueName)

                paramList.add(valueName)
            }

            methodBuilderBase.addParameter(Int::class.java, "resultCode")
                    .addParameter(Int::class.java, "flags")
                    .addStatement("$classActivityIntent activityIntent = prepareIntent($activityClass.class, parameterList)")
                    .addStatement("navigationService.performNavigation(activityIntent, resultCode, flags)")

            var methodClassParams = "show$activityName("
            paramList.forEach {
                methodClassParams += "$it, "
            }

            methodBuilderOverloadMethodNotEmpty.addParameter(Int::class.java, "resultCode")
                    .addStatement(methodClassParams + "resultCode, 0)")
            methodBuilderOverloadMethodEmpty.addStatement(methodClassParams + "0, 0)")

            fileBuilder.addMethod(methodBuilderOverloadMethodEmpty.build())
            fileBuilder.addMethod(methodBuilderOverloadMethodNotEmpty.build())
            fileBuilder.addMethod(methodBuilderBase.build())
        }
    }
}
