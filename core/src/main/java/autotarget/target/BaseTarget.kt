package autotarget.target

import android.os.Bundle
import autotarget.parameter.ParameterProvider

/**
 * Abstract class that is initialising the target bundle that is used by any child class.
 *
 * @param parameters List of parameters for the target object.
 *
 * @since 1.0.0
 */
abstract class BaseTarget(parameters: List<ParameterProvider>) {

    var bundle: Bundle = Bundle()
        private set

    init {
        for (parameter in parameters) parameter.addToBundle(bundle)
    }
}