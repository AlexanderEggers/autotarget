package autotarget.util

import autotarget.MainProcessor
import javax.annotation.processing.RoundEnvironment

interface AnnotationProcessor {
    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment)
}