package org.autointent.annotation

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class FragmentTarget(val state: Int,
                                val addToBackStack: Boolean = true)