package autotarget.target

import android.os.Bundle
import autotarget.parameter.ParameterProvider

abstract class BaseTarget(parameters: List<ParameterProvider>) {

    var bundle: Bundle = Bundle()
        private set

    init {
        for (parameter in parameters) parameter.addToBundle(bundle)
    }
}