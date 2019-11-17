package autotarget.parameter

import android.os.Bundle

/**
 * This interface defines how specific values are added to the [Bundle] object. The [Bundle] is
 * then used to deliver parameters to the new target object.
 *
 * @since 1.0.0
 */
interface ParameterProvider {
    /**
     * Adds values to the given [Bundle] object.
     *
     * @param bundle [Bundle] object that will receive the new values
     */
    fun addToBundle(bundle: Bundle)
}