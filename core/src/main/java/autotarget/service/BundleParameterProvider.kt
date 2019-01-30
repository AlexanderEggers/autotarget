package autotarget.service

import android.os.Bundle

open class BundleParameterProvider(val key: String, val value: Bundle?): ParameterProvider {

    override fun addToBundle(bundle: Bundle) {
        bundle.putBundle(key, value)
    }
}