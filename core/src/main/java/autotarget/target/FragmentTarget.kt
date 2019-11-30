package autotarget.target

import android.app.Activity
import androidx.fragment.app.Fragment
import autotarget.parameter.ParameterProvider
import autotarget.util.FragmentDispatcher

/**
 * This class defines a target that will lead to a new [Fragment].
 *
 * @param fragment Instance of the new [Fragment].
 * @param tag [String] that can be used to determine if there is already a fragment for this target
 * existing or not. To use the tag, you need to extend your [Activity] with [FragmentDispatcher]. By
 * default this parameter is just an empty string.
 * @param state Helps to identify [Fragment] to retrieve or create an instance.
 * @param enterAnim An animation or animator resource ID used for the enter animation on the
 * view of the fragment being added or attached.
 * @param exitAnim An animation or animator resource ID used for the exit animation on the
 * view of the fragment being removed or detached.
 * @param popEnterAnim An animation or animator resource ID used for the enter animation on
 * the view of the fragment being readded or reattached caused by
 * {@link FragmentManager#popBackStack()} or similar methods.
 * @param popExitAnim An animation or animator resource ID used for the enter animation on the
 * view of the fragment being removed or detached caused by {@link FragmentManager#popBackStack()}
 * or similar methods.
 *
 * @since 1.0.0
 */
open class FragmentTarget(val fragment: Fragment,
                          val containerId: Int,
                          val tag: String,
                          val state: Int,
                          val enterAnim: Int,
                          val exitAnim: Int,
                          val popEnterAnim: Int,
                          val popExitAnim: Int,
                          parameters: List<ParameterProvider>) : BaseTarget(parameters) {

    init {
        fragment.arguments = super.bundle
    }
}