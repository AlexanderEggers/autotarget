package org.autointent.annotation

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ForFragmentIntent(val state: Int,
                                   val addToBackStack: Boolean = true)