package org.autointent.annotation

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class IntentParameter(val valueKey: String,
                                 val valueName : String = "unspecified",
                                 val valueType: KClass<*>,
                                 val parameterType: ParameterType)
