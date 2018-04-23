package org.autointent.annotation

import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TargetParameter(val key: String,
                                 val name : String = "unspecified",
                                 val type: KClass<*>,
                                 val optional: Boolean = false)
