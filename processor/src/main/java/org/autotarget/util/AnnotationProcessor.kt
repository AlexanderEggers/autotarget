package org.autotarget.util

import org.autotarget.MainProcessor
import javax.annotation.processing.RoundEnvironment

interface AnnotationProcessor {
    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment)
}