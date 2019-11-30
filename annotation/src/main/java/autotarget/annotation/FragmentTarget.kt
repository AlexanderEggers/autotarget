package autotarget.annotation

/**
 * This annotation is used to declare a Fragment as a potential navigation target. Based on this
 * annotation, the related annotation processor is adding methods to the FragmentTargets and
 * FragmentBundles classes.
 *
 * @property containerId View based resource [Integer] that is used to define the View container
 * that is used to attach the Fragment to.
 * @property tag [String] that defines the tag for this Fragment.
 * @property state [Integer] that can help identifying a Fragment.
 * @property enterAnim An animation or animator resource ID used for the enter animation on
 * the view of the fragment being added or attached.
 * @property exitAnim An animation or animator resource ID used for the exit animation on the
 * view of the fragment being removed or detached.
 * @property popEnterAnim An animation or animator resource ID used for the enter animation on
 * the view of the fragment being readded or reattached caused by
 * {@link FragmentManager#popBackStack()} or similar methods.
 * @property popExitAnim An animation or animator resource ID used for the enter animation on
 * the view of the fragment being removed or detached caused by
 * {@link FragmentManager#popBackStack()} or similar methods.
 *
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class FragmentTarget(
        val containerId: Int = -1,
        val tag: String = "",
        val state: Int = -1,
        val enterAnim: Int = -1,
        val exitAnim: Int = -1,
        val popEnterAnim: Int = -1,
        val popExitAnim: Int = -1)