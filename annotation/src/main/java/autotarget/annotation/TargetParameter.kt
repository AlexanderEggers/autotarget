package autotarget.annotation

/**
 * This annotation is used to define the required/optional parameters for the given
 * activity/fragment. This annotation needs to be used in combination with a [ActivityTarget] or
 * [FragmentTarget] annotation. The parameters defined using this annotation will be added to the
 * generated classes of the [ActivityTarget] and [FragmentTarget] annotations. A parameter can be
 * any type of value that is going to define the state the target (activity/fragment). Some
 * parameters are required - others might be optional (e.g. showing a section of page only if a
 * certain optional parameter is set).
 *
 * @property value [Array] of [TargetParameterItem] that is used by the annotation generator to
 * create the type-safe target methods.
 *
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TargetParameter(val value: Array<TargetParameterItem>)
