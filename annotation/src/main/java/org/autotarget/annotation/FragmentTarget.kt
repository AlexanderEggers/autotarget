package org.autotarget.annotation

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class FragmentTarget(val containerId: Int,
                                val state: Int = -1,
                                val tag: String = "undefined")