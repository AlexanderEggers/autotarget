package org.autointent.helper

import com.squareup.javapoet.*
import org.autointent.util.HelperProcessor
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class ContextProviderProcessor : HelperProcessor {

    private val classContext = ClassName.get("android.content", "Context")

    override fun process(filer: Filer) {
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
                        .addModifiers(Modifier.PROTECTED)
                        .addParameter(classContext, "context")
                        .addStatement("this.context = context")
                        .build())

        val file = fileBuilder.build()
        JavaFile.builder("org.autointent.generated", file)
                .build()
                .writeTo(filer)
    }
}
