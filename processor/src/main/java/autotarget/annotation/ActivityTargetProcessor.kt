package autotarget.annotation

import autotarget.ProcessorUtil
import autotarget.ProcessorUtil.classActivityTarget
import autotarget.ProcessorUtil.classArrayList
import autotarget.ProcessorUtil.classNonNull
import autotarget.ProcessorUtil.populateParamListBody
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ActivityTargetProcessor {

    private val activitiesWithPackage: HashMap<String, String> = HashMap()
    private var targetParameterMap: HashMap<String, Element> = HashMap()
    private var activityAnnotationMap: HashMap<String, Element> = HashMap()

    fun process(processingEnv: ProcessingEnvironment,
                roundEnv: RoundEnvironment,
                targetParameterMap: HashMap<String, Element>) {

        this.targetParameterMap = targetParameterMap

        val fileBuilder = TypeSpec.classBuilder("ActivityTargets")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

        preparePackageMap(processingEnv, roundEnv)
        createMethods(processingEnv, fileBuilder)

        val file = fileBuilder.build()
        JavaFile.builder(ProcessorUtil.libraryGeneratedPackageName, file)
                .build()
                .writeTo(processingEnv.filer)
    }

    private fun preparePackageMap(processingEnv: ProcessingEnvironment, roundEnv: RoundEnvironment) {
        for (it in roundEnv.getElementsAnnotatedWith(ActivityTarget::class.java)) {
            if (!it.kind.isClass) {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR,
                        "Can only be applied to a class. Error for ${it.simpleName}")
                continue
            }

            val typeElement = it as TypeElement
            activitiesWithPackage[typeElement.simpleName.toString()] =
                    processingEnv.elementUtils.getPackageOf(typeElement).qualifiedName.toString()
            activityAnnotationMap[typeElement.simpleName.toString()] = it
        }
    }

    private fun createMethods(processingEnv: ProcessingEnvironment, fileBuilder: TypeSpec.Builder) {
        activitiesWithPackage.forEach { (activityName, packageName) ->
            val annotationElement: Element = activityAnnotationMap[activityName]!!
            val enterAnimation = annotationElement.getAnnotation(ActivityTarget::class.java).enterAnimation
            val exitAnimation = annotationElement.getAnnotation(ActivityTarget::class.java).exitAnimation

            val activityClass = ClassName.get(packageName, activityName)
            val parameterMap = ProcessorUtil.createTargetParameterMap(annotationElement)

            parameterMap.keys.forEach {
                val forDefaultGroup = it == ProcessorUtil.libraryDefaultGroupKey
                        || it == ProcessorUtil.libraryOptionalGroupKey
                val parameterItems = parameterMap[it] ?: ArrayList()

                val methodName = if (forDefaultGroup) "show${activityName}"
                else "show${activityName}For${it.toLowerCase().capitalize()}"

                val methodBuilder = MethodSpec.methodBuilder(methodName)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addAnnotation(classNonNull)
                        .returns(classActivityTarget)
                        .addStatement("${ProcessorUtil.listOfParameterProvider} parameterList = new $classArrayList<>()")

                populateParamListBody(processingEnv, parameterItems, methodBuilder)
                methodBuilder.addStatement("return new $classActivityTarget(" +
                        "$activityClass.class, $enterAnimation, $exitAnimation, parameterList)")
                fileBuilder.addMethod(methodBuilder.build())
            }
        }
    }
}
