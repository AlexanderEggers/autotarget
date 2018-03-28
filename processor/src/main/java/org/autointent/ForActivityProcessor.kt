package org.autointent

import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import com.squareup.javapoet.ClassName
import org.autointent.annotation.ForActivity
import org.autointent.annotation.IntentParameter
import org.autointent.util.ProcessorUtil
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic
import com.squareup.javapoet.FieldSpec
import org.autointent.util.ContextProviderProcessorUtil

@AutoService(Processor::class)
class ForActivityProcessor : AbstractProcessor() {

    private var contextProviderClass: TypeName = ClassName.get("org.autointent.generated", "ContextProvider")
    private val classIntent: TypeName = ClassName.get("android.content", "Intent")

    private var filer: Filer? = null
    private var messager: Messager? = null
    private var elements: Elements? = null

    private var intentParameterMap: HashMap<String, ArrayList<Element>>? = null
    private var activitiesWithPackage: HashMap<String, String>? = null

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ForActivity::class.java.name, IntentParameter::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        filer = processingEnvironment.filer
        messager = processingEnvironment.messager
        elements = processingEnvironment.elementUtils

        intentParameterMap = HashMap()
        activitiesWithPackage = HashMap()
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        try {
            processIntentParameters(roundEnv)
            ContextProviderProcessorUtil().createContextProvider(roundEnv, filer)

            val fileBuilder = TypeSpec.classBuilder("IntentService")
                    .addModifiers(Modifier.PUBLIC)
                    .addField(FieldSpec.builder(contextProviderClass, "contextProvider")
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                            .initializer("new $contextProviderClass()")
                            .build())

            val check = processForActivity(roundEnv, fileBuilder)
            if(check) return true

            val file = fileBuilder.build()
            JavaFile.builder("org.autointent.generated", file)
                    .build()
                    .writeTo(filer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return true
    }

    private fun processIntentParameters(roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(IntentParameter::class.java)
                .forEach {
                    val className = it.simpleName.toString()
                    var elements = intentParameterMap!![className]

                    if (elements == null) {
                        elements = ArrayList()
                    }

                    elements.add(it)
                    intentParameterMap!![className] = elements
                }
    }

    private fun processForActivity(roundEnv: RoundEnvironment, fileBuilder: TypeSpec.Builder): Boolean {
        roundEnv.getElementsAnnotatedWith(ForActivity::class.java)
                .forEach {
                    if (it.kind != ElementKind.CLASS) {
                        messager!!.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.")
                        return true
                    }

                    val typeElement = it as TypeElement
                    activitiesWithPackage!![typeElement.simpleName.toString()] =
                            elements!!.getPackageOf(typeElement).qualifiedName.toString()
                }

        activitiesWithPackage!!.forEach { activityName, packageName ->
            val activityClass = ClassName.get(packageName, activityName)

            fileBuilder.addMethod(MethodSpec.methodBuilder("show$activityName")
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("contextProvider.getContext().startActivity(new $classIntent(contextProvider.getContext(), $activityClass.class))")
                    .addParameter(ClassName.get(ProcessorUtil.getType(activityName, intentParameterMap!!)), "param")
                    .addJavadoc("Test" + intentParameterMap!![activityName]!![0].getAnnotation(IntentParameter::class.java).valueKey)
                    .build())
        }

        return false
    }
}