package org.autointent

import com.google.auto.service.AutoService
import org.autointent.annotation.ForActivity
import org.autointent.annotation.ForActivityProcessor
import org.autointent.annotation.IntentParameter
import org.autointent.annotation.IntentParameterProcessor
import org.autointent.helper.*
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

@AutoService(Processor::class)
class MainProcessor : AbstractProcessor() {

    var filer: Filer? = null
    var messager: Messager? = null
    var elements: Elements? = null
    var intentParameterMap: HashMap<String, ArrayList<Element>>? = null

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        filer = processingEnvironment.filer
        messager = processingEnvironment.messager
        elements = processingEnvironment.elementUtils
        intentParameterMap = HashMap()
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        try {
            //Helper processor part - like for the class ContextProvider
            ContextProviderProcessor().process(filer!!)
            ContextInjectorProcessor().process(filer!!)
            ParameterProviderProcessor().process(filer!!)

            //Annotation processor part - like for the annotation @ForActivity
            IntentParameterProcessor().process(this, roundEnv)
            ForActivityProcessor().process(this, roundEnv)

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return true
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ForActivity::class.java.name, IntentParameter::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }
}