package org.autointent.helper

import com.squareup.javapoet.*
import org.autointent.util.HelperProcessor
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class NavigatorServiceProcessor : HelperProcessor {

    private val classActivityIntent = ClassName.get("org.autointent.generated", "ActivityIntent")
    private val contextProviderClass: TypeName = ClassName.get("org.autointent.generated", "ContextProvider")
    private val contextInjectorClass: TypeName = ClassName.get("org.autointent.generated", "ContextInjector")

    private val classIntent = ClassName.get("android.content", "Intent")
    private val classBundle = ClassName.get("android.os", "Bundle")
    private val classActivity = ClassName.get("android.app", "Activity")

    override fun process(filer: Filer) {
        val fileBuilder = TypeSpec.classBuilder("NavigationService")
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(contextProviderClass, "contextProvider")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("$contextInjectorClass.getInstance()")
                        .build())
                .addMethod(MethodSpec.methodBuilder("performNavigation")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(classActivityIntent, "target")
                        .addParameter(Int::class.java, "requestCode")
                        .addParameter(Int::class.java, "flags")
                        .addCode("$classIntent intent = new $classIntent(contextProvider.getContext(), target.getActivityClass());\n" +
                                "intent.addFlags(flags);\n" +
                                "\n" +
                                "$classBundle bundle = new $classBundle();\n" +
                                "for (ParameterProvider parameter : target.getParameterList()) {\n" +
                                "   parameter.addToBundle(bundle);\n" +
                                "}\n" +
                                "\n" +
                                "intent.putExtras(bundle);\n" +
                                "if (requestCode > 0) {\n" +
                                "   (($classActivity) contextProvider.getContext()).startActivityForResult(intent, requestCode);\n" +
                                "} else {\n" +
                                "   contextProvider.getContext().startActivity(intent);\n" +
                                "}\n")
                        .build())

        val file = fileBuilder.build()
        JavaFile.builder("org.autointent.generated", file)
                .build()
                .writeTo(filer)
    }
}
