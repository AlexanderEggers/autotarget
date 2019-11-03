package autotarget.annotation

/**
 * This annotation is to define required/optional parameters for the given activity/fragment. This
 * annotation needs to be used in combination with the [ActivityTarget] and [FragmentTarget]
 * annotations. The parameters defined using this annotation will be added to the generated classes
 * of the [ActivityTarget] and [FragmentTarget] annotations. A parameter can be any type of value
 * that is going to define the state the target (activity/fragment) is going to be. Some parameter
 * are required and always expected by the target. Other parameter might be optional and could
 * define e.g. sections of that target which will only be shown if certain optional parameter are
 * set.
 *
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TargetParameter(
        /**
         * Returns an [Array] of [TargetParameterItem] that is used by the annotation generator to
         * create the type-safe methods.
         */
        val value: Array<TargetParameterItem>)
