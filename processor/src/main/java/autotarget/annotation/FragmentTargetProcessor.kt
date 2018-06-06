package autotarget.annotation

import autotarget.MainProcessor
import autotarget.ProcessorUtil.classArrayList
import autotarget.ProcessorUtil.classFragmentTarget
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

class FragmentTargetProcessor {

    private val listOfParameterProvider = ParameterizedTypeName.get(classList, classParameterProvider)
    private val arrayListOfParameterProvider = ParameterizedTypeName.get(classArrayList, classParameterProvider)

    private val fragmentsWithPackage: HashMap<String, String> = HashMap()
    private var targetParameterMap: HashMap<String, ArrayList<Element>>? = null
    private var fragmentAnnotationMap: HashMap<String, Element> = HashMap()

    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        targetParameterMap = mainProcessor.targetParameterMap

        val fileBuilder = TypeSpec.classBuilder("FragmentTargets")
                .addModifiers(Modifier.PUBLIC)

        preparePackageMap(mainProcessor, roundEnv)
        createMethods(fileBuilder)

        val file = fileBuilder.build()
        JavaFile.builder("autotarget.generated", file)
                .build()
                .writeTo(mainProcessor.filer)
    }

    private fun preparePackageMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        for (it in roundEnv.getElementsAnnotatedWith(FragmentTarget::class.java)) {
            if (!it.kind.isClass) {
                mainProcessor.messager!!.printMessage(Diagnostic.Kind.ERROR,
                        "Can only be applied to a class. Error inside ${it.simpleName}")
                continue
            }

            val typeElement = it as TypeElement
            fragmentsWithPackage[typeElement.simpleName.toString()] =
                    mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString()
            fragmentAnnotationMap[typeElement.simpleName.toString()] = it
        }
    }

    private fun createMethods(fileBuilder: TypeSpec.Builder) {
        fragmentsWithPackage.forEach { fragmentName, packageName ->
            val baseList: ArrayList<Element> = ArrayList()
            val optionalList: ArrayList<Element> = ArrayList()

            val annotationElement: Element = fragmentAnnotationMap[fragmentName]!!
            val state = annotationElement.getAnnotation(FragmentTarget::class.java).state
            val containerId = annotationElement.getAnnotation(FragmentTarget::class.java).containerId
            val tag = annotationElement.getAnnotation(FragmentTarget::class.java).tag

            val fragmentClass = ClassName.get(packageName, fragmentName)

            targetParameterMap!![fragmentName]?.forEach {
                val isOptional = it.getAnnotation(TargetParameter::class.java).optional

                if (isOptional) {
                    optionalList.add(it)
                } else {
                    baseList.add(it)
                }
            }

            if (!baseList.isEmpty() || optionalList.isEmpty()) {
                val methodBuilderBase = MethodSpec.methodBuilder("show$fragmentName")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addAnnotation(classNonNull)
                        .returns(classFragmentTarget)

                if (!baseList.isEmpty()) {
                    methodBuilderBase.addStatement("$listOfParameterProvider parameterList = new $classArrayList<>()")
                    populateParamListBody(baseList, methodBuilderBase, 0)
                    methodBuilderBase.addStatement("return new $classFragmentTarget(" +
                            "new $fragmentClass(), $state, $containerId, \"$tag\", parameterList)")
                } else {
                    methodBuilderBase.addStatement("return new $classFragmentTarget(" +
                            "new $fragmentClass(), $state, $containerId, \"$tag\", new $arrayListOfParameterProvider())")
                }

                fileBuilder.addMethod(methodBuilderBase.build())
            }

            if (!optionalList.isEmpty()) {
                val methodBuilderWithOptionals = MethodSpec.methodBuilder("show$fragmentName")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addAnnotation(classNonNull)
                        .returns(classFragmentTarget)
                        .addStatement("$listOfParameterProvider parameterList = new $classArrayList<>()")

                val paramCountOptional = populateParamListBody(baseList, methodBuilderWithOptionals, 0)
                populateParamListBody(optionalList, methodBuilderWithOptionals, paramCountOptional)

                methodBuilderWithOptionals.addStatement("return new $classFragmentTarget(" +
                        "new $fragmentClass(), $state, $containerId, \"$tag\", parameterList)")
                fileBuilder.addMethod(methodBuilderWithOptionals.build())
            }
        }
    }
}
