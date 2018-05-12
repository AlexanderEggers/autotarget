package autotarget.annotation

import autotarget.MainProcessor
import autotarget.util.AnnotationProcessor
import autotarget.util.ProcessorUtil.classArrayList
import autotarget.util.ProcessorUtil.classList
import autotarget.util.ProcessorUtil.classNonNull
import autotarget.util.ProcessorUtil.classParameterProvider
import autotarget.util.ProcessorUtil.populateParamListBody
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class FragmentTargetProcessor : AnnotationProcessor {

    private val classFragmentTarget = ClassName.get("autotarget.service", "FragmentTarget")

    private val listOfParameterProvider = ParameterizedTypeName.get(classList(), classParameterProvider())
    private val arrayListOfParameterProvider = ParameterizedTypeName.get(classArrayList(), classParameterProvider())

    private val fragmentsWithPackage: HashMap<String, String> = HashMap()
    private var targetParameterMap: HashMap<String, ArrayList<Element>>? = null
    private var fragmentAnnotationMap: HashMap<String, Element> = HashMap()

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
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
        roundEnv.getElementsAnnotatedWith(FragmentTarget::class.java).forEach {
            if (it.kind != ElementKind.CLASS) {
                mainProcessor.messager!!.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.")
                return
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
                        .addAnnotation(classNonNull())
                        .returns(classFragmentTarget)

                if (!baseList.isEmpty()) {
                    methodBuilderBase.addStatement("$listOfParameterProvider parameterList = new ${classArrayList()}<>()")
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
                        .addAnnotation(classNonNull())
                        .returns(classFragmentTarget)
                        .addStatement("$listOfParameterProvider parameterList = new ${classArrayList()}<>()")

                val paramCountOptional = populateParamListBody(baseList, methodBuilderWithOptionals, 0)
                populateParamListBody(optionalList, methodBuilderWithOptionals, paramCountOptional)

                methodBuilderWithOptionals.addStatement("return new $classFragmentTarget(" +
                        "new $fragmentClass(), $state, $containerId, \"$tag\", parameterList)")
                fileBuilder.addMethod(methodBuilderWithOptionals.build())
            }
        }
    }
}
