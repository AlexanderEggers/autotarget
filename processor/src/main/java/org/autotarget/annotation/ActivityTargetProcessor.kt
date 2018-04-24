package org.autotarget.annotation

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import org.autotarget.MainProcessor
import org.autotarget.util.AnnotationProcessor
import org.autotarget.util.ProcessorUtil.classArrayList
import org.autotarget.util.ProcessorUtil.classList
import org.autotarget.util.ProcessorUtil.classNonNull
import org.autotarget.util.ProcessorUtil.classParameterProvider
import org.autotarget.util.ProcessorUtil.populateParamListBody
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ActivityTargetProcessor : AnnotationProcessor {

    private val classActivityTarget = ClassName.get("org.autotarget.service", "ActivityTarget")

    private val listOfParameterProvider = ParameterizedTypeName.get(classList(), classParameterProvider())
    private val arrayListOfParameterProvider = ParameterizedTypeName.get(classArrayList(), classParameterProvider())

    private val activitiesWithPackage: HashMap<String, String> = HashMap()
    private var targetParameterMap: HashMap<String, ArrayList<Element>>? = null

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        targetParameterMap = mainProcessor.targetParameterMap

        val fileBuilder = TypeSpec.classBuilder("ActivityTargets")
                .addModifiers(Modifier.PUBLIC)

        preparePackageMap(mainProcessor, roundEnv)
        createMethods(fileBuilder)

        val file = fileBuilder.build()
        JavaFile.builder("org.autotarget.generated", file)
                .build()
                .writeTo(mainProcessor.filer)
    }

    private fun preparePackageMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(ActivityTarget::class.java).forEach {
            if (it.kind != ElementKind.CLASS) {
                mainProcessor.messager!!.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.")
                return
            }

            val typeElement = it as TypeElement
            activitiesWithPackage[typeElement.simpleName.toString()] =
                    mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString()
        }
    }

    private fun createMethods(fileBuilder: TypeSpec.Builder) {
        activitiesWithPackage.forEach { activityName, packageName ->
            val baseList: ArrayList<Element> = ArrayList()
            val optionalList: ArrayList<Element> = ArrayList()

            val activityClass = ClassName.get(packageName, activityName)

            targetParameterMap!![activityName]?.forEach {
                val isOptional = it.getAnnotation(TargetParameter::class.java).optional

                if (isOptional) {
                    optionalList.add(it)
                } else {
                    baseList.add(it)
                }
            }

            if (!baseList.isEmpty() || optionalList.isEmpty()) {
                val methodBuilderBase = MethodSpec.methodBuilder("show$activityName")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addAnnotation(classNonNull())
                        .returns(classActivityTarget)

                if (!baseList.isEmpty()) {
                    methodBuilderBase.addStatement("$listOfParameterProvider parameterList = new ${classArrayList()}<>()")
                    populateParamListBody(baseList, methodBuilderBase, 0)
                    methodBuilderBase.addStatement("return new $classActivityTarget($activityClass.class, parameterList)")
                } else {
                    methodBuilderBase.addStatement("return new $classActivityTarget($activityClass.class, new $arrayListOfParameterProvider())")
                }

                fileBuilder.addMethod(methodBuilderBase.build())
            }

            if (!optionalList.isEmpty()) {
                val methodBuilderWithOptionals = MethodSpec.methodBuilder("show$activityName")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addAnnotation(classNonNull())
                        .returns(classActivityTarget)
                        .addStatement("$listOfParameterProvider parameterList = new ${classArrayList()}<>()")

                val paramCountOptional = populateParamListBody(baseList, methodBuilderWithOptionals, 0)
                populateParamListBody(optionalList, methodBuilderWithOptionals, paramCountOptional)

                methodBuilderWithOptionals.addStatement("return new $classActivityTarget($activityClass.class, parameterList)")
                fileBuilder.addMethod(methodBuilderWithOptionals.build())
            }
        }
    }
}
