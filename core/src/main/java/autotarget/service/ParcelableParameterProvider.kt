package autotarget.service

import android.os.Bundle
import android.os.Parcelable

open class ParcelableParameterProvider(private val key: String, private val value: Parcelable?): ParameterProvider {

    override fun addToBundle(bundle: Bundle) {
        bundle.putParcelable(key, value)
    }
}