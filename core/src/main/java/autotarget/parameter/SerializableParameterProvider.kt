package autotarget.parameter

import android.os.Bundle
import java.io.Serializable

/**
 * This class defines how a [Serializable] object is added to a specific target [Bundle] object.
 *
 * @param key Identifier that is used to save the [Serializable] object
 * @param value [Serializable] object that should be saved
 *
 * @since 1.0.0
 */
open class SerializableParameterProvider(val key: String, val value: Serializable?) : ParameterProvider {

    override fun addToBundle(bundle: Bundle) {
        bundle.putSerializable(key, value)
    }
}