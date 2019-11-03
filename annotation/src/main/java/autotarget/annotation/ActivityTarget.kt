package autotarget.annotation

/**
 * This annotation is used to declare an Activity as a potential navigation target. Based on this
 * annotation, the related annotation processor is adding methods to the ActivityTargets and
 * ActivityBundles classes.
 *
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ActivityTarget(
        /**
         * Returns an animation based resource [Integer] that is used to define the enter animation
         * for this activity.
         */
        val enterAnimation: Int = -1,

        /**
         * Returns an animation based resource [Integer] that is used to define the exit animation
         * for this activity.
         */
        val exitAnimation: Int = -1)