package org.autointent.processor

import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import org.autointent.annotation.ForActivity
import org.autointent.annotation.IntentParameter
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Filer
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.MirroredTypeException

@AutoService(Processor::class)
class ForActivityProcessor : AbstractProcessor() {

    private val classIntent = ClassName.get("java.lang", "String")
    private var filer: Filer? = null
    private val intentParameterMap = HashMap<String, ArrayList<Element>>()

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ForActivity::class.java.name, IntentParameter::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    @Synchronized override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        filer = processingEnvironment.filer
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(IntentParameter::class.java)
                .forEach {
                    val className = it.simpleName.toString()
                    var elements = intentParameterMap[className]

                    if(elements == null) {
                        elements = ArrayList()
                    }

                    elements.add(it)
                    intentParameterMap[className] = elements
                }

        roundEnv.getElementsAnnotatedWith(ForActivity::class.java)
                .forEach {
                    val className = it.simpleName.toString()
                    generateClass(className)
                }
        return true
    }

    private fun generateClass(className: String) {
        val fileName = "Generated_$className"
        val file = TypeSpec.classBuilder(fileName)
                .addModifiers(Modifier.PUBLIC)
                        .addMethod(MethodSpec.methodBuilder("getName")
                                .addStatement("return \"World\"")
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(ClassName.get(getType(className)), "param")
                                .addJavadoc("Test" + intentParameterMap[className]!![0].getAnnotation(IntentParameter::class.java).valueKey)
                                .returns(classIntent)
                                .build())
                        .build()

        JavaFile.builder("org.autointent.generated", file)
                .build()
                .writeTo(filer)
    }

    private fun getType(className: String): TypeMirror? {
        try {
            intentParameterMap[className]!![0].getAnnotation(IntentParameter::class.java).valueType
        } catch(mte: MirroredTypeException) {
            return mte.typeMirror
        }
        return null
    }
}