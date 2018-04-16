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

    private val classParameterProvider = ClassName.get("org.autointent.generated", "ParameterProvider")
    private val contextProviderClass: TypeName = ClassName.get("org.autointent.generated", "ContextProvider")
    private val contextInjectorClass: TypeName = ClassName.get("org.autointent.generated", "ContextInjector")

    private val classIntent = ClassName.get("android.content", "Intent")
    private val classBundle = ClassName.get("android.os", "Bundle")
    private val classActivity = ClassName.get("android.app", "Activity")
    private val classContext = ClassName.get("android.content", "Context")
    private val classNonNull = ClassName.get("android.support.annotation", "NonNull")
    private val classNullable = ClassName.get("android.support.annotation", "Nullable")

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
                .addField(FieldSpec.builder(contextProviderClass, "contextProvider")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("$contextInjectorClass.getInstance()")
                        .build())

        prepareActivityPackageMap(mainProcessor, roundEnv)

        createMethodsForActivities(fileBuilder)
        createPerformNavigationMethod(fileBuilder)
        createFinishActivityMethod(fileBuilder)
        createFinishActivityWithResult(fileBuilder)

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
            val paramList: ArrayList<String> = ArrayList()

            val methodBuilderBase = MethodSpec.methodBuilder("show$activityName")
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("$listOfParameterProvider parameterList = new $classArrayList<>()")
            val methodBuilderOverloadMethodNotEmpty = MethodSpec.methodBuilder("show$activityName")
                    .addModifiers(Modifier.PUBLIC)
            val methodBuilderOverloadMethodEmpty = MethodSpec.methodBuilder("show$activityName")
                    .addModifiers(Modifier.PUBLIC)

            intentParameterMap!![activityName]?.forEach {
                var valueName = it.getAnnotation(IntentParameter::class.java).valueName
                val valueKey = it.getAnnotation(IntentParameter::class.java).valueKey
                val isNonNull = it.getAnnotation(IntentParameter::class.java).isNonNull

                if (valueName == "unspecified") {
                    valueName = "param$paramCount"
                    paramCount++
                }

                val parameter = ParameterSpec.builder(ClassName.get(ProcessorUtil.getType(it)), valueName)
                        .addAnnotation(if (isNonNull) classNonNull else classNullable)
                        .build()

                methodBuilderBase.addParameter(parameter)
                        .addStatement("parameterList.add(new $classParameterProvider(\"$valueKey\", $valueName))")
                methodBuilderOverloadMethodNotEmpty.addParameter(parameter)
                methodBuilderOverloadMethodEmpty.addParameter(parameter)

                paramList.add(valueName)
            }

            methodBuilderBase.addParameter(Int::class.java, "requestCode")
                    .addParameter(Int::class.java, "flags")
                    .addStatement("performNavigation($activityClass.class, parameterList, requestCode, flags)")

            var methodClassParams = "show$activityName("
            paramList.forEach {
                methodClassParams += "$it, "
            }

            methodBuilderOverloadMethodNotEmpty.addParameter(Int::class.java, "flags")
                    .addStatement(methodClassParams + "0, flags)")
            methodBuilderOverloadMethodEmpty.addStatement(methodClassParams + "0, 0)")

            fileBuilder.addMethod(methodBuilderOverloadMethodEmpty.build())
            fileBuilder.addMethod(methodBuilderOverloadMethodNotEmpty.build())
            fileBuilder.addMethod(methodBuilderBase.build())
        }
    }

    private fun createPerformNavigationMethod(fileBuilder: TypeSpec.Builder) {
        fileBuilder.addMethod(MethodSpec.methodBuilder("performNavigation")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(classClass, "target")
                .addParameter(listOfParameterProvider, "parameterList")
                .addParameter(Int::class.java, "requestCode")
                .addParameter(Int::class.java, "flags")
                .addCode("$classIntent intent = new $classIntent(contextProvider.getContext(), target);\n" +
                        "intent.addFlags(flags);\n" +
                        "\n" +
                        "$classBundle bundle = new $classBundle();\n" +
                        "for (ParameterProvider parameter : parameterList) {\n" +
                        "   parameter.addToBundle(bundle);\n" +
                        "}\n" +
                        "\n" +
                        "intent.putExtras(bundle);\n\n" +
                        "if (requestCode > 0) {\n" +
                        "   (($classActivity) contextProvider.getContext()).startActivityForResult(intent, requestCode);\n" +
                        "} else {\n" +
                        "   contextProvider.getContext().startActivity(intent);\n" +
                        "}\n")
                .build())
    }

    private fun createFinishActivityMethod(fileBuilder: TypeSpec.Builder) {
        fileBuilder.addMethod(MethodSpec.methodBuilder("finish")
                .addModifiers(Modifier.PUBLIC)
                .addCode("$classContext context = contextProvider.getContext();\n" +
                        "if (context != null && context instanceof $classActivity) {\n" +
                        "   (($classActivity) context).finish();\n" +
                        "}\n")
                .build())
    }

    private fun createFinishActivityWithResult(fileBuilder: TypeSpec.Builder) {
        fileBuilder.addMethod(MethodSpec.methodBuilder("finishWithResult")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Int::class.java, "resultCode")
                .addCode("$classContext context = contextProvider.getContext();\n" +
                        "if (context != null && context instanceof $classActivity) {\n" +
                        "   (($classActivity) context).setResult(resultCode);\n" +
                        "   (($classActivity) context).finish();\n" +
                        "}\n")
                .build())
    }
}
