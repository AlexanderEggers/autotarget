package autotarget.parameter

import android.os.Bundle
import android.os.Parcelable

open class ParcelableParameterProvider(val key: String, val value: Parcelable?) : ParameterProvider {

    override fun addToBundle(bundle: Bundle) {
        bundle.putParcelable(key, value)
    }
}