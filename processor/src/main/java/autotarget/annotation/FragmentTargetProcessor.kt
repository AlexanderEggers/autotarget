package autotarget.annotation

import autotarget.ProcessorUtil
import autotarget.ProcessorUtil.classArrayList
import autotarget.ProcessorUtil.classFragmentTarget
import autotarget.ProcessorUtil.classList
import autotarget.ProcessorUtil.classNonNull
import autotarget.ProcessorUtil.classParameterProvider
import autotarget.ProcessorUtil.populateParamListBody
import com.squareup.javapoet.*
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class FragmentTargetProcessor {

    private val listOfParameterProvider = ParameterizedTypeName.get(classList, classParameterProvider)
    private val arrayListOfParameterProvider = ParameterizedTypeName.get(classArrayList, classParameterProvider)

    private val fragmentsWithPackage: HashMap<String, String> = HashMap()
    private var targetParameterMap: HashMap<String, Element> = HashMap()
    private var fragmentAnnotationMap: HashMap<String, Element> = HashMap()

    fun process(processingEnv: ProcessingEnvironment,
                roundEnv: RoundEnvironment,
                targetParameterMap: HashMap<String, Element>) {

        this.targetParameterMap = targetParameterMap

        val fileBuilder = TypeSpec.classBuilder("FragmentTargets")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

        preparePackageMap(processingEnv, roundEnv)
        createMethods(processingEnv, fileBuilder)

        val file = fileBuilder.build()
        JavaFile.builder("autotarget.generated", file)
                .build()
                .writeTo(processingEnv.filer)
    }

    private fun preparePackageMap(processingEnv: ProcessingEnvironment, roundEnv: RoundEnvironment) {
        for (it in roundEnv.getElementsAnnotatedWith(FragmentTarget::class.java)) {
            if (!it.kind.isClass) {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR,
                        "Can only be applied to a class. Error for ${it.simpleName}")
                continue
            }

            val typeElement = it as TypeElement
            fragmentsWithPackage[typeElement.simpleName.toString()] =
                    processingEnv.elementUtils.getPackageOf(typeElement).qualifiedName.toString()
            fragmentAnnotationMap[typeElement.simpleName.toString()] = it
        }
    }

    private fun createMethods(processingEnv: ProcessingEnvironment, fileBuilder: TypeSpec.Builder) {
        fragmentsWithPackage.forEach { (fragmentName, packageName) ->
            val annotationElement: Element = fragmentAnnotationMap[fragmentName]!!
            val state = annotationElement.getAnnotation(FragmentTarget::class.java).state
            val containerId = annotationElement.getAnnotation(FragmentTarget::class.java).containerId
            val tag = annotationElement.getAnnotation(FragmentTarget::class.java).tag

            val enterAnimation = annotationElement.getAnnotation(FragmentTarget::class.java).enterAnimation
            val exitAnimation = annotationElement.getAnnotation(FragmentTarget::class.java).exitAnimation
            val popEnterAnimation = annotationElement.getAnnotation(FragmentTarget::class.java).popEnterAnimation
            val popExitAnimation = annotationElement.getAnnotation(FragmentTarget::class.java).popExitAnimation

            val fragmentClass = ClassName.get(packageName, fragmentName)
            val targetParameter = targetParameterMap[fragmentName]?.getAnnotation(TargetParameter::class.java)
            val parameterMap = ProcessorUtil.createTargetParameterMap(annotationElement)

            val forceEmptyTargetMethod = targetParameter?.forceEmptyTargetMethod ?: false
            if (forceEmptyTargetMethod || parameterMap.isEmpty()) createDefaultTargetMethod(
                    fragmentClass, fragmentName, state, containerId, tag, enterAnimation,
                    exitAnimation, popEnterAnimation, popExitAnimation, fileBuilder)

            parameterMap.keys.forEach {
                val parameterItems = parameterMap[it]
                if (parameterItems?.isNotEmpty() == true) {
                    val methodBuilderWithOptionals = MethodSpec.methodBuilder("show${fragmentName}For${it.capitalize()}")
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                            .addAnnotation(classNonNull)
                            .returns(classFragmentTarget)
                            .addStatement("$listOfParameterProvider parameterList = new $classArrayList<>()")

                    populateParamListBody(processingEnv, parameterItems, methodBuilderWithOptionals)
                    methodBuilderWithOptionals.addStatement("return new $classFragmentTarget(" +
                            "new $fragmentClass(), $state, $containerId, \"$tag\", $enterAnimation, " +
                            "$exitAnimation, $popEnterAnimation, $popExitAnimation, parameterList)")
                    fileBuilder.addMethod(methodBuilderWithOptionals.build())
                }
            }
        }
    }

    private fun createDefaultTargetMethod(fragmentClass: ClassName, fragmentName: String, state: Int,
                                          containerId: Int, tag: String, enterAnimation: Int,
                                          exitAnimation: Int, popEnterAnimation: Int,
                                          popExitAnimation: Int, fileBuilder: TypeSpec.Builder) {

        val methodBuilderBase = MethodSpec.methodBuilder("show$fragmentName")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addAnnotation(classNonNull)
                .returns(classFragmentTarget)
                .addStatement("return new $classFragmentTarget(" +
                        "new $fragmentClass(), $state, $containerId, \"$tag\", $enterAnimation, " +
                        "$exitAnimation, $popEnterAnimation, $popExitAnimation, " +
                        "new $arrayListOfParameterProvider())")

        fileBuilder.addMethod(methodBuilderBase.build())
    }
}
