package org.autointent.util

import android.content.Context
import java.lang.ref.WeakReference

class ContextProvider internal constructor() {
    private var context: WeakReference<Context>? = null

    fun getContext(): Context? {
        return context?.get()
    }

    fun setContext(context: Context) {
        this.context = WeakReference(context)
    }
}