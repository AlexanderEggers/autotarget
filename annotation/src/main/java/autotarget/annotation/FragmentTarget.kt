package autotarget.annotation

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class FragmentTarget(val containerId: Int = -1,
                                val state: Int = -1,
                                val tag: String = "undefined",
                                val enterAnimation: Int = -1,
                                val exitAnimation: Int = -1,
                                val popEnterAnimation: Int = -1,
                                val popExitAnimation: Int = -1)