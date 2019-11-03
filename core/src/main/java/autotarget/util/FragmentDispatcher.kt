package autotarget.util

import android.os.Bundle
import autotarget.service.FragmentTarget

interface FragmentDispatcher {
    fun showFragment(containerId: Int, state: Enum<*>, bundle: Bundle?): Boolean
    fun showFragment(containerId: Int, state: Int, bundle: Bundle?): Boolean
    fun showFragment(target: FragmentTarget?, containerId: Int, state: Int, bundle: Bundle?): Boolean
}