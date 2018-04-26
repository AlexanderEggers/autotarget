package org.autotarget.util

import android.os.Bundle

interface HasFragmentFlow {
    fun onShowNextFragment(containerId: Int, state: Int, addToBackStack: Boolean,
                           clearBackStack: Boolean, bundle: Bundle?): Boolean
}