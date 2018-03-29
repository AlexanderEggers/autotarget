package org.autointent.helper

import com.squareup.javapoet.*
import org.autointent.util.HelperProcessor
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class ContextInjectorProcessor : HelperProcessor {

    private var contextProviderClass: TypeName = ClassName.get("org.autointent.generated", "ContextProvider")
    private val classContext = ClassName.get("android.content", "Context")

    override fun process(filer: Filer) {
        val fileBuilder = TypeSpec.classBuilder("ContextInjector")
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(contextProviderClass, "contextProvider")
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                        .build())
                .addMethod(MethodSpec.methodBuilder("getInstance")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.SYNCHRONIZED)
                        .addCode(""
                                + "if(contextProvider == null) {\n"
                                + "  contextProvider = new $contextProviderClass();\n"
                                + "}\n"
                                + "return contextProvider;\n")
                        .returns(contextProviderClass)
                        .build())
                .addMethod(MethodSpec.methodBuilder("inject")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.SYNCHRONIZED)
                        .addParameter(classContext, "context")
                        .addStatement("getInstance().setContext(context)")
                        .build())

        val file = fileBuilder.build()
        JavaFile.builder("org.autointent.generated", file)
                .build()
                .writeTo(filer)
    }
}
