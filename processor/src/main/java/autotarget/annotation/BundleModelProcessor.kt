package autotarget.annotation

import autotarget.MainProcessor
import autotarget.ProcessorUtil.populateBundleModel
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

abstract class BundleModelProcessor {

    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment): HashMap<String, ClassName> {
        val annotationMap = preparePackageMap(mainProcessor, roundEnv)
        return createClasses(mainProcessor, annotationMap)
    }

    private fun preparePackageMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment): HashMap<String, Element> {
        val annotationMap = HashMap<String, Element>()

        for (it in roundEnv.getElementsAnnotatedWith(getElementAnnotationClass())) {
            if (!it.kind.isClass) {
                mainProcessor.messager.printMessage(Diagnostic.Kind.ERROR,
                        "Can only be applied to a class. Error for ${it.simpleName}")
                continue
            }

            val typeElement = it as TypeElement
            annotationMap[typeElement.simpleName.toString()] = it
        }

        return annotationMap
    }

    private fun createClasses(mainProcessor: MainProcessor, annotationMap: HashMap<String, Element>): HashMap<String, ClassName> {
        val bundleModelMap = HashMap<String, ClassName>()

        annotationMap.forEach { (className, annotationElement) ->
            val targetParameter = annotationElement.getAnnotation(TargetParameter::class.java)

            val parameterList = ArrayList<TargetParameterItem>()
            targetParameter?.value?.forEach { parameterList.add(it) }

            val fileBuilder = TypeSpec.classBuilder("${className}BundleModel")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

            populateBundleModel(parameterList, fileBuilder)

            val file = fileBuilder.build()
            JavaFile.builder("autotarget.generated", file)
                    .build()
                    .writeTo(mainProcessor.filer)

            bundleModelMap[className] = ClassName.get(
                    "autotarget.generated", "${className}BundleModel")
        }

        return bundleModelMap
    }

    abstract fun <T: Annotation> getElementAnnotationClass(): Class<T>
}
