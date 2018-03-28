package org.autointent.util

import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Modifier
import javax.annotation.processing.Filer

class ContextProviderProcessorUtil {

    private val classContext = ClassName.get("android.content", "Context")

    fun createContextProvider(roundEnv: RoundEnvironment, filer: Filer?) {
        val fileBuilder = TypeSpec.classBuilder("ContextProvider")
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(classContext, "context")
                        .addModifiers(Modifier.PRIVATE)
                        .build())
                .addMethod(MethodSpec.methodBuilder("getContext")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return context")
                        .returns(classContext)
                        .build())
                .addMethod(MethodSpec.methodBuilder("setContext")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(classContext, "context")
                        .addStatement("this.context = context")
                        .build())

        val file = fileBuilder.build()
        JavaFile.builder("org.autointent.generated", file)
                .build()
                .writeTo(filer)
    }
}
