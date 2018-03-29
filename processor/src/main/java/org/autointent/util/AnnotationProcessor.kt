package org.autointent.util

import org.autointent.MainProcessor
import javax.annotation.processing.RoundEnvironment

interface AnnotationProcessor {
    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment)
}