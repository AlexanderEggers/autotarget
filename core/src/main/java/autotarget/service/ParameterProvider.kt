package autotarget.service

import android.os.Bundle
import java.io.Serializable

class ParameterProvider(private val key: String, private val value: Serializable) {

    fun addToBundle(bundle: Bundle) {
        bundle.putSerializable(key, value)
    }
}