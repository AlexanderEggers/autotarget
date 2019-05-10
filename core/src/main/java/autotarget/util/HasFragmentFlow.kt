package autotarget.util

import android.os.Bundle

interface HasFragmentFlow {
    fun onShowNextFragment(containerId: Int, state: Int, bundle: Bundle?): Boolean
}