package autotarget.service

import android.app.Activity

open class ActivityTarget(val targetClass: Class<out Activity>, val enterAnimation: Int,
                          val exitAnimation: Int, parameterList: List<ParameterProvider>) {

    val parameters: Array<ParameterProvider> = parameterList.toTypedArray()
}
