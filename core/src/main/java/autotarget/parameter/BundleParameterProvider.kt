package autotarget.parameter

import android.os.Bundle

/**
 * This class defines how a [Bundle] object is added to a specific target [Bundle] object.
 *
 * @param key Identifier that is used to save the [Bundle] object
 * @param value [Bundle] object that should be saved
 *
 * @since 1.0.0
 */
open class BundleParameterProvider(val key: String, val value: Bundle?) : ParameterProvider {

    override fun addToBundle(bundle: Bundle) {
        bundle.putBundle(key, value)
    }
}