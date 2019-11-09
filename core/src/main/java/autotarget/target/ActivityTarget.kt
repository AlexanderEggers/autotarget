package autotarget.target

import android.app.Activity
import autotarget.parameter.ParameterProvider

/**
 * This class defines a target that will lead to a new [Activity].
 *
 * @param targetClass Class of the [Activity] that this target will lead to.
 * @param enterAnimation Resource [Int] that defines the enter animation of the new [Activity].
 * @param exitAnimation Resource [Int] that defines the exit animation of the new [Activity].
 * @param parameters List of [ParameterProvider] that are parsed to the new [Activity].
 */
open class ActivityTarget(val targetClass: Class<out Activity>,
                          val enterAnimation: Int,
                          val exitAnimation: Int,
                          parameters: List<ParameterProvider>) : BaseTarget(parameters)
