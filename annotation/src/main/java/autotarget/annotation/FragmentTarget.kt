package autotarget.annotation

/**
 * This annotation is used to declare a Fragment as a potential navigation target. Based on this
 * annotation, the related annotation processor is adding methods to the FragmentTargets and
 * FragmentBundles classes.
 *
 * @property containerId View based resource [Integer] that is used to define the View container
 * that is used to attach the Fragment to.
 * @property tag [String] that defines the tag for this Fragment.
 * @property enterAnimation Animation based resource [Integer] that is used to define the enter
 * animation for this Fragment.
 * @property exitAnimation Animation based resource [Integer] that is used to define the exit
 * animation for this Fragment.
 * @property popEnterAnimation Animation based resource [Integer] that is used to define the
 * pop-enter animation for this fragment.
 * @property popExitAnimation Animation based resource [Integer] that is used to define the
 * pop-exit animation for this fragment.
 *
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class FragmentTarget(
        val containerId: Int = -1,
        val tag: String = "undefined",
        val enterAnimation: Int = -1,
        val exitAnimation: Int = -1,
        val popEnterAnimation: Int = -1,
        val popExitAnimation: Int = -1)