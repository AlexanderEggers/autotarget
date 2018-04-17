package org.autointent.helper

import com.squareup.javapoet.*
import org.autointent.util.HelperProcessor
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class HasFragmentFlowProcessor : HelperProcessor {

    private val classInteger = ClassName.get("java.lang", "Integer")
    private val classBundle = ClassName.get("android.os", "Bundle")
    private val classBoolean = ClassName.get("java.lang", "Boolean")

    override fun process(filer: Filer) {
        val fileBuilder = TypeSpec.interfaceBuilder("HasFragmentFlow")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(MethodSpec.methodBuilder("onShowNextFragment")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(classInteger, "state")
                        .addParameter(classBoolean, "addToBackStack")
                        .addParameter(classBundle, "bundle")
                        .build())

        val file = fileBuilder.build()
        JavaFile.builder("org.autointent.generated", file)
                .build()
                .writeTo(filer)
    }
}
