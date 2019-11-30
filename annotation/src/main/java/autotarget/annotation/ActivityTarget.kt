package autotarget.annotation

/**
 * This annotation is used to declare an Activity as a potential navigation target. Based on this
 * annotation, the related annotation processor is adding methods to the ActivityTargets and
 * ActivityBundles classes.
 *
 * @property enterAnim A resource ID of the animation resource to use for the new Activity.
 * Use 0 for no animation.
 * @property exitAnim A resource ID of the animation resource to use for the current Activity.
 * Use 0 for no animation.
 *
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ActivityTarget(
        val enterAnim: Int = -1,
        val exitAnim: Int = -1)