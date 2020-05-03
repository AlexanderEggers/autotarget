package autotarget

import autotarget.annotation.*
import com.google.auto.service.AutoService
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import java.io.IOException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class MainProcessor : AbstractProcessor() {

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        try {
            if (set.isNotEmpty()) {
                val targetParameterMap = TargetParameterProcessor().process(processingEnv, roundEnv)

                ActivityTargetProcessor().process(processingEnv, roundEnv, targetParameterMap)
                val activityBundleClasses = ActivityBundleModelProcessor().process(processingEnv, roundEnv)
                ActivityBundleProviderProcessor().process(processingEnv, activityBundleClasses)

                FragmentTargetProcessor().process(processingEnv, roundEnv, targetParameterMap)
                val fragmentBundleClasses = FragmentBundleModelProcessor().process(processingEnv, roundEnv)
                FragmentBundleProviderProcessor().process(processingEnv, fragmentBundleClasses)
            }
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