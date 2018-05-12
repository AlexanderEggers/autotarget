package autotarget.service

import android.app.Activity

class ActivityTarget(val targetClass: Class<out Activity>, parameterList: List<ParameterProvider>) {
    val parameters: Array<ParameterProvider> = parameterList.toTypedArray()
}
