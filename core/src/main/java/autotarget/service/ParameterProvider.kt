package autotarget.service

import android.os.Bundle
import java.io.Serializable

open class ParameterProvider(private val key: String, private val value: Serializable?) {

    open fun addToBundle(bundle: Bundle) {
        bundle.putSerializable(key, value)
    }
}