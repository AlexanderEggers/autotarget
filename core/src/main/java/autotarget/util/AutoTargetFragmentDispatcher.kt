package autotarget.util

import android.os.Bundle
import autotarget.service.FragmentTarget

interface AutoTargetFragmentDispatcher {
    fun onShowNextFragment(target: FragmentTarget, containerId: Int, state: Int, bundle: Bundle?): Boolean
}