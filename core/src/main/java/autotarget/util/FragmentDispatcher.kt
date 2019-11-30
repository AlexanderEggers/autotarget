package autotarget.util

import autotarget.target.FragmentTarget
import androidx.fragment.app.FragmentManager
import android.app.Activity
import androidx.fragment.app.Fragment
import autotarget.target.TargetService

/**
 * Interface that is used to provide a custom implementation for dispatching new [Fragment]s to the
 * [FragmentManager]. This interface needs to be added to the [Activity] which will add new
 * [Fragment]s.
 *
 * @since 1.0.0
 */
interface FragmentDispatcher {

    /**
     * Attempts to show a [Fragment] using the given [FragmentTarget].
     *
     * @param target Meta object that is used to show a [Fragment]
     *
     * @return True if the method was able to successfully show a [Fragment], false otherwise. If
     * the [Boolean] is false, the [TargetService] will attempt to show a [Fragment] using the
     * given [FragmentTarget].
     *
     * @since 1.0.0
     */
    fun showFragment(target: FragmentTarget?): Boolean
}