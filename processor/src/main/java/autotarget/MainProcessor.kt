package autotarget

import autotarget.annotation.*
import com.google.auto.service.AutoService
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

@AutoService(Processor::class)
class MainProcessor : AbstractProcessor() {

    lateinit var filer: Filer
    lateinit var messager: Messager
    lateinit var elements: Elements

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
            val targetParameterMap = TargetParameterProcessor().process(this, roundEnv)

            ActivityTargetProcessor().process(this, roundEnv, targetParameterMap)
            val activityBundleClasses = ActivityBundleModelProcessor().process(this, roundEnv)

            FragmentTargetProcessor().process(this, roundEnv, targetParameterMap)
            val fragmentBundleClasses = FragmentBundleModelProcessor().process(this, roundEnv)

        } catch (e: IOException) {
            e.printStackTrace()
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