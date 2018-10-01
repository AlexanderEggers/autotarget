package autotarget.service

import android.os.Bundle

open class BundleParameterProvider(private val key: String, private val value: Bundle?): ParameterProvider {

    override fun addToBundle(bundle: Bundle) {
        bundle.putBundle(key, value)
    }
}