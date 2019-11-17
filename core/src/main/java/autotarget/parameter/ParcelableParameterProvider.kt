package autotarget.parameter

import android.os.Bundle
import android.os.Parcelable

/**
 * This class defines how a [Parcelable] object is added to a specific target [Bundle] object.
 *
 * @param key Identifier that is used to save the [Parcelable] object
 * @param value [Parcelable] object that should be saved
 *
 * @since 1.0.0
 */
open class ParcelableParameterProvider(val key: String, val value: Parcelable?) : ParameterProvider {

    override fun addToBundle(bundle: Bundle) {
        bundle.putParcelable(key, value)
    }
}