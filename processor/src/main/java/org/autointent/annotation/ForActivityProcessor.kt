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

    private val classIntent: TypeName = ClassName.get("android.content", "Intent")

    private val activitiesWithPackage: HashMap<String, String> = HashMap()
    private var intentParameterMap: HashMap<String, ArrayList<Element>>? = null

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        intentParameterMap = mainProcessor.intentParameterMap

        val fileBuilder = TypeSpec.classBuilder("ActivityService")
                .addModifiers(Modifier.PUBLIC)
                .addField(ProcessorUtil.createContextProviderField())

        prepareActivityPackageMap(mainProcessor, roundEnv)
        createMethodsForActivities(fileBuilder)

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

    private fun createMethodsForActivities(fileBuilder: TypeSpec.Builder) {
        activitiesWithPackage.forEach { activityName, packageName ->
            val activityClass = ClassName.get(packageName, activityName)

            fileBuilder.addMethod(MethodSpec.methodBuilder("show$activityName")
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("contextProvider.getContext().startActivity(new $classIntent(contextProvider.getContext(), $activityClass.class))")
                    .addParameter(ClassName.get(ProcessorUtil.getType(activityName, intentParameterMap!!)), "param")
                    .addJavadoc("Test" + intentParameterMap!![activityName]!![0].getAnnotation(IntentParameter::class.java).valueKey)
                    .build())
        }
    }
}
