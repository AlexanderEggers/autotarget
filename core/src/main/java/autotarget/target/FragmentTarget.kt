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
 * @param enterAnimation Resource [Int] that defines the enter animation of the new [Fragment].
 * @param exitAnimation Resource [Int] that defines the exit animation of the new [Fragment].
 * @param popEnterAnimation Resource [Int] that defines the popEnter animation of the new
 * [Fragment].
 * @param popExitAnimation Resource [Int] that defines the popExit animation of the new [Fragment].
 * @param parameters List of [ParameterProvider] that are parsed to the new [Fragment].
 *
 * @since 1.0.0
 */
open class FragmentTarget(val fragment: Fragment,
                          val containerId: Int,
                          val tag: String,
                          val state: Int,
                          val enterAnimation: Int,
                          val exitAnimation: Int,
                          val popEnterAnimation: Int,
                          val popExitAnimation: Int,
                          parameters: List<ParameterProvider>) : BaseTarget(parameters) {

    init {
        fragment.arguments = super.bundle
    }
}