package autotarget

import autotarget.annotation.*
import com.google.auto.service.AutoService
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

@AutoService(Processor::class)
class MainProcessor : AbstractProcessor() {

    lateinit var filer: Filer
    lateinit var messager: Messager
    lateinit var elements: Elements
    val targetParameterMap: HashMap<String, ArrayList<Element>> = HashMap()

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        filer = processingEnvironment.filer
        messager = processingEnvironment.messager
        elements = processingEnvironment.elementUtils
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        try {
            //Annotation processor part - like for the annotation @ActivityTarget
            TargetParameterProcessor().process(this, roundEnv)
            ActivityTargetProcessor().process(this, roundEnv)
            FragmentTargetProcessor().process(this, roundEnv)
        } catch (e: IOException) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Something went wrong: ${e.message}")
        }

        return true
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ActivityTarget::class.java.name, FragmentTarget::class.java.name,
                TargetParameter::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }
}