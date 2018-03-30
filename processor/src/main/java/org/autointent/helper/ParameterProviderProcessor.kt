package org.autointent.helper

import com.squareup.javapoet.*
import org.autointent.util.HelperProcessor
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class ParameterProviderProcessor : HelperProcessor {

    private val classBundle = ClassName.get("android.os", "Bundle")
    private val classString = ClassName.get("java.lang", "String")
    private val classSerializable = ClassName.get("java.io", "Serializable")

    override fun process(filer: Filer) {
        val fileBuilder = TypeSpec.classBuilder("ParameterProvider")
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(classString, "key")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build())
                .addField(FieldSpec.builder(classSerializable, "value")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PROTECTED)
                        .addParameter(classString, "key")
                        .addParameter(classSerializable, "value")
                        .addCode("this.key = key;\n" +
                                "this.value = value;")
                        .build())
                .addMethod(MethodSpec.methodBuilder("getKey")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return key")
                        .returns(classString)
                        .build())
                .addMethod(MethodSpec.methodBuilder("getValue")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return value")
                        .returns(classSerializable)
                        .build())
                .addMethod(MethodSpec.methodBuilder("addToBundle")
                        .addModifiers(Modifier.PROTECTED)
                        .addParameter(classBundle, "bundle")
                        .addStatement("bundle.putSerializable(key, value)")
                        .build())

        val file = fileBuilder.build()
        JavaFile.builder("org.autointent.generated", file)
                .build()
                .writeTo(filer)
    }
}
