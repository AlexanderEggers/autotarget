package autotarget.annotation

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ActivityTarget(val enterAnimation: Int = -1,
                                val exitAnimation: Int = -1)