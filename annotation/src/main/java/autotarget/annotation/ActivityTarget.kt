package autotarget.annotation

/**
 * This annotation is used to declare an Activity as a potential navigation target. Based on this
 * annotation, the related annotation processor is adding methods to the ActivityTargets and
 * ActivityBundles classes.
 *
 * @param enterAnimation Animation based resource [Integer] that is used to define the enter
 * animation for this activity.
 * @param exitAnimation Animation based resource [Integer] that is used to define the exit
 * animation for this activity.
 *
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ActivityTarget(
        val enterAnimation: Int = -1,
        val exitAnimation: Int = -1)