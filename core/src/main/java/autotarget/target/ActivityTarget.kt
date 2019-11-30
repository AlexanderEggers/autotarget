package autotarget.target

import android.app.Activity
import autotarget.parameter.ParameterProvider

/**
 * This class defines a target that will lead to a new [Activity].
 *
 * @param targetClass Class of the [Activity] that this target will lead to.
 * @param enterAnim A resource ID of the animation resource to use for the new [Activity]. Use
 * 0 for no animation.
 * @param exitAnim A resource ID of the animation resource to use for the current [Activity].
 * Use 0 for no animation.
 * @param parameters List of [ParameterProvider] that are parsed to the new [Activity].
 *
 * @since 1.0.0
 */
open class ActivityTarget(val targetClass: Class<out Activity>,
                          val enterAnim: Int,
                          val exitAnim: Int,
                          parameters: List<ParameterProvider>) : BaseTarget(parameters)
