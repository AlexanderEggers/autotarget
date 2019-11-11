package autotarget.annotation

/**
 * This annotation is to define required/optional parameters for the given activity/fragment. This
 * annotation needs to be used in combination with a [ActivityTarget] or [FragmentTarget]
 * annotation. The parameters defined using this annotation will be added to the generated classes
 * of the [ActivityTarget] and [FragmentTarget] annotations. A parameter can be any type of value
 * that is going to define the state the target (activity/fragment). Some parameter are required.
 * Other parameter might be optional and could define e.g. sections of that target which will only
 * be shown if certain optional parameter are set.
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
