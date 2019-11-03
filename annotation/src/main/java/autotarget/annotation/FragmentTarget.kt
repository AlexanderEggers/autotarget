package autotarget.annotation

/**
 * This annotation is used to declare a Fragment as a potential navigation target. Based on this
 * annotation, the related annotation processor is adding methods to the FragmentTargets and
 * FragmentBundles classes.
 *
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class FragmentTarget(
        /**
         * Returns an view based resource [Integer] that is used to define the view container that is
         * used to attach the fragment to.
         */
        val containerId: Int = -1,

        /**
         * Returns a state [Integer] that is used to find this fragment out of multiple different
         * fragments.
         */
        val state: Int = -1,

        /**
         * Returns a [String] that defines the tag for this fragment.
         */
        val tag: String = "undefined",

        /**
         * Returns an animation based resource [Integer] that is used to define the enter animation
         * for this fragment.
         */
        val enterAnimation: Int = -1,

        /**
         * Returns an animation based resource [Integer] that is used to define the exit animation
         * for this fragment.
         */
        val exitAnimation: Int = -1,

        /**
         * Returns an animation based resource [Integer] that is used to define the pop-enter
         * animation for this fragment.
         */
        val popEnterAnimation: Int = -1,

        /**
         * Returns an animation based resource [Integer] that is used to define the pop-exit
         * animation for this fragment.
         */
        val popExitAnimation: Int = -1)