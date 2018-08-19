package autotarget.service

import android.support.v4.app.Fragment

open class FragmentTarget(val fragment: Fragment, val state: Int, val containerId: Int, val tag: String, parameterList: List<ParameterProvider>) {
    val parameters: Array<ParameterProvider> = parameterList.toTypedArray()
}
