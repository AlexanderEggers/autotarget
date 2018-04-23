package org.autotarget.util

import android.os.Bundle
import android.support.annotation.Nullable

interface HasFragmentFlow {
    fun onShowNextFragment(state: Int, addToBackStack: Boolean, @Nullable bundle: Bundle)
}