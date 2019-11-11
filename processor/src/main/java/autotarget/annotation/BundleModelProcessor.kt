package autotarget.annotation

import autotarget.ProcessorUtil
import autotarget.ProcessorUtil.populateBundleModel
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

abstract class BundleModelProcessor {

    fun process(processingEnv: ProcessingEnvironment, roundEnv: RoundEnvironment): HashMap<String, ClassName> {
        val annotationMap = preparePackageMap(processingEnv, roundEnv)
        return createClasses(processingEnv, annotationMap)
    }

    private fun preparePackageMap(processingEnv: ProcessingEnvironment, roundEnv: RoundEnvironment): HashMap<String, Element> {
        val annotationMap = HashMap<String, Element>()

        for (it in roundEnv.getElementsAnnotatedWith(getElementAnnotationClass())) {
            if (!it.kind.isClass) {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR,
                        "Can only be applied to a class. Error for ${it.simpleName}")
                continue
            }

            val typeElement = it as TypeElement
            annotationMap[typeElement.simpleName.toString()] = it
        }

        return annotationMap
    }

    private fun createClasses(processingEnv: ProcessingEnvironment, annotationMap: HashMap<String, Element>): HashMap<String, ClassName> {
        val bundleModelMap = HashMap<String, ClassName>()

        annotationMap.forEach { (className, annotationElement) ->
            val targetParameter = annotationElement.getAnnotation(TargetParameter::class.java)
            val bundleModelName = "${className}BundleModel"
            val parameterList = targetParameter?.value?.toList() ?: ArrayList()

            val fileBuilder = TypeSpec.classBuilder(bundleModelName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            populateBundleModel(processingEnv, parameterList, fileBuilder)

            val file = fileBuilder.build()
            JavaFile.builder(ProcessorUtil.libraryGeneratedPackageName, file)
                    .build()
                    .writeTo(processingEnv.filer)

            bundleModelMap[bundleModelName] = ClassName.get(
                    ProcessorUtil.libraryGeneratedPackageName,
                    bundleModelName)
        }

        return bundleModelMap
    }

    abstract fun <T : Annotation> getElementAnnotationClass(): Class<T>
}
