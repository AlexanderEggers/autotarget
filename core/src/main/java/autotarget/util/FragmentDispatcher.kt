package autotarget.util

import autotarget.target.FragmentTarget

interface FragmentDispatcher {
    fun showFragment(target: FragmentTarget?): Boolean
}