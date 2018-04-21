package org.autointent.annotation

import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class IntentParameter(val valueKey: String,
                                 val valueName : String = "unspecified",
                                 val valueType: KClass<*>,
                                 val isNonNull: Boolean = true)
