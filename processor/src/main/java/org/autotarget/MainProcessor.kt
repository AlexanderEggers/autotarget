package org.autotarget

import com.google.auto.service.AutoService
import org.autotarget.annotation.ActivityTarget
import org.autotarget.annotation.ForActivityIntentProcessor
import org.autotarget.annotation.TargetParameter
import org.autotarget.annotation.TargetParameterProcessor
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
    var targetParameterMap: HashMap<String, ArrayList<Element>>? = null

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        filer = processingEnvironment.filer
        messager = processingEnvironment.messager
        elements = processingEnvironment.elementUtils
        targetParameterMap = HashMap()
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        try {
            //Annotation processor part - like for the annotation @ActivityTarget
            TargetParameterProcessor().process(this, roundEnv)
            ForActivityIntentProcessor().process(this, roundEnv)

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return true
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ActivityTarget::class.java.name, TargetParameter::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }
}