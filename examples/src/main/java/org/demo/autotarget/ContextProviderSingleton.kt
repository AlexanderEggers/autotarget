package org.demo.autotarget

import archknife.context.ContextProvider

class ContextProviderSingleton {
     companion object {

         @JvmStatic
         val contextProvider: ContextProvider = ContextProvider()
     }
}