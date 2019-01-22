package autotarget.service

import androidx.fragment.app.Fragment

open class FragmentTarget(val fragment: Fragment, val state: Int, val containerId: Int, val tag: String,
                          val enterAnimation: Int, val exitAnimation: Int, val popEnterAnimation: Int,
                          val popExitAnimation: Int, parameterList: List<ParameterProvider>) {

    val parameters: Array<ParameterProvider> = parameterList.toTypedArray()
}
