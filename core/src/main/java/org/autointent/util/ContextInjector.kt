package org.autointent.util

import android.content.Context

object ContextInjector {

    val contextProvider: ContextProvider = ContextProvider()

    @Synchronized
    fun inject(context: Context) {
        contextProvider.setContext(context)
    }
}
