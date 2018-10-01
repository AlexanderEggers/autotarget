package autotarget.service

import android.os.Bundle
import java.io.Serializable

open class ValueParameterProvider(private val key: String, private val value: Serializable?): ParameterProvider {

    override fun addToBundle(bundle: Bundle) {
        bundle.putSerializable(key, value)
    }
}