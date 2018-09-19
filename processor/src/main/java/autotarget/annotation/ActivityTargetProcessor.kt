package autotarget.annotation

import autotarget.MainProcessor
import autotarget.ProcessorUtil.classActivityTarget
import autotarget.ProcessorUtil.classArrayList
import autotarget.ProcessorUtil.classList
import autotarget.ProcessorUtil.classNonNull
import autotarget.ProcessorUtil.classParameterProvider
import autotarget.ProcessorUtil.populateParamListBody
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ActivityTargetProcessor {

    private val listOfParameterProvider = ParameterizedTypeName.get(classList, classParameterProvider)
    private val arrayListOfParameterProvider = ParameterizedTypeName.get(classArrayList, classParameterProvider)

    private val activitiesWithPackage: HashMap<String, String> = HashMap()
    private var targetParameterMap: HashMap<String, Element>? = null

    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        targetParameterMap = mainProcessor.targetParameterMap

        val fileBuilder = TypeSpec.classBuilder("ActivityTargets")
                .addModifiers(Modifier.PUBLIC)

        preparePackageMap(mainProcessor, roundEnv)
        createMethods(fileBuilder)

        val file = fileBuilder.build()
        JavaFile.builder("autotarget.generated", file)
                .build()
                .writeTo(mainProcessor.filer)
    }

    private fun preparePackageMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        for (it in roundEnv.getElementsAnnotatedWith(ActivityTarget::class.java)) {
            if (!it.kind.isClass) {
                mainProcessor.messager.printMessage(Diagnostic.Kind.ERROR,
                        "Can only be applied to a class. Error for ${it.simpleName}")
                continue
            }

            val typeElement = it as TypeElement
            activitiesWithPackage[typeElement.simpleName.toString()] =
                    mainProcessor.elements.getPackageOf(typeElement).qualifiedName.toString()
        }
    }

    private fun createMethods(fileBuilder: TypeSpec.Builder) {
        activitiesWithPackage.forEach { activityName, packageName ->
            val baseList: ArrayList<TargetParameterItem> = ArrayList()
            val optionalList: ArrayList<TargetParameterItem> = ArrayList()

            val activityClass = ClassName.get(packageName, activityName)
            targetParameterMap?.get(activityName)?.getAnnotation(TargetParameter::class.java)?.value?.forEach {
                if (it.optional) {
                    optionalList.add(it)
                } else {
                    baseList.add(it)
                }
            }

            if (!baseList.isEmpty() || optionalList.isEmpty()) {
                val methodBuilderBase = MethodSpec.methodBuilder("show$activityName")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addAnnotation(classNonNull)
                        .returns(classActivityTarget)

                if (!baseList.isEmpty()) {
                    methodBuilderBase.addStatement("$listOfParameterProvider parameterList = new $classArrayList<>()")
                    populateParamListBody(baseList, methodBuilderBase, 0)
                    methodBuilderBase.addStatement("return new $classActivityTarget($activityClass.class, parameterList)")
                } else {
                    methodBuilderBase.addStatement("return new $classActivityTarget($activityClass.class, new $arrayListOfParameterProvider())")
                }

                fileBuilder.addMethod(methodBuilderBase.build())
            }

            if (!optionalList.isEmpty()) {
                val methodBuilderWithOptionals = MethodSpec.methodBuilder("show$activityName")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addAnnotation(classNonNull)
                        .returns(classActivityTarget)
                        .addStatement("$listOfParameterProvider parameterList = new $classArrayList<>()")

                val paramCountOptional = populateParamListBody(baseList, methodBuilderWithOptionals, 0)
                populateParamListBody(optionalList, methodBuilderWithOptionals, paramCountOptional)

                methodBuilderWithOptionals.addStatement("return new $classActivityTarget($activityClass.class, parameterList)")
                fileBuilder.addMethod(methodBuilderWithOptionals.build())
            }
        }
    }
}
