package org.autointent.helper

import com.squareup.javapoet.*
import org.autointent.util.HelperProcessor
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName

class ActivityIntentProcessor : HelperProcessor {

    private val classParameterProvider = ClassName.get("org.autointent.generated", "ParameterProvider")
    private val classClass = ClassName.get("java.lang", "Class")
    private val classArrayList = ClassName.get("java.util", "ArrayList")
    private val classList = ClassName.get("java.util", "List")

    private val listOfParameterProvider: TypeName = ParameterizedTypeName.get(classList, classParameterProvider)

    override fun process(filer: Filer) {
        val fileBuilder = TypeSpec.classBuilder("ActivityIntent")
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(classClass, "activityClass")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build())
                .addField(FieldSpec.builder(listOfParameterProvider, "parameterList")
                        .initializer("new $classArrayList<>()")
                        .addModifiers(Modifier.PRIVATE)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PROTECTED)
                        .addParameter(classClass, "activityClass")
                        .addParameter(listOfParameterProvider, "parameterList")
                        .addCode("this.activityClass = activityClass;\n" +
                                "this.parameterList = parameterList;\n")
                        .build())
                .addMethod(MethodSpec.methodBuilder("getActivityClass")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return activityClass")
                        .returns(classClass)
                        .build())
                .addMethod(MethodSpec.methodBuilder("getParameterList")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return parameterList")
                        .returns(listOfParameterProvider)
                        .build())

        val file = fileBuilder.build()
        JavaFile.builder("org.autointent.generated", file)
                .build()
                .writeTo(filer)
    }
}
