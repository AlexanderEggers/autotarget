package autotarget.annotation

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TargetParameter(val value: Array<TargetParameterItem>,
                                 val forceEmptyTargetMethod: Boolean = false)
