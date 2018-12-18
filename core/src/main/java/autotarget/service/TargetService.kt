package autotarget.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.util.Log
import archknife.context.ContextProvider
import archknife.context.ContextProviderCommunicator
import autotarget.util.HasFragmentFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class TargetService @Inject constructor() {

    private val contextProvider: ContextProviderCommunicator? = ContextProvider

    @JvmOverloads
    open fun execute(target: ActivityTarget, flags: Int = 0, requestCode: Int = 0,
                     context: Context? = contextProvider?.activityContext) {

        val intent = create(target, flags, requestCode)
        if (context != null && context is Activity && requestCode > 0) {
            context.startActivityForResult(intent, requestCode)

            val enterAnimation = target.enterAnimation
            val exitAnimation = target.exitAnimation
            if(enterAnimation != -1 && exitAnimation != -1) {
                context.overridePendingTransition(target.enterAnimation, target.exitAnimation)
            }
        } else context?.startActivity(intent)
    }

    @JvmOverloads
    open fun execute(target: FragmentTarget, containerId: Int = target.containerId,
                     addToBackStack: Boolean = true, clearBackStack: Boolean = false,
                     context: Context? = contextProvider?.activityContext) {

        val bundle = Bundle()
        for (parameter in target.parameters) { parameter.addToBundle(bundle) }

        if (containerId == -1) {
            Log.e(TargetService::class.java.name, "Container ID cannot be -1. Check your " +
                    "annotation or set a custom container id using the execute method.")
        } else {
            if (context != null && context is HasFragmentFlow && target.state != -1) {
                val check = context.onShowNextFragment(containerId, target.state, addToBackStack, clearBackStack, bundle)

                if (!check && context is FragmentActivity) {
                    showFragmentAsDefault(target, containerId, addToBackStack, clearBackStack, bundle, context)
                }
            } else if (context != null && context is FragmentActivity) {
                showFragmentAsDefault(target, containerId, addToBackStack, clearBackStack, bundle, context)
            }
        }
    }

    @JvmOverloads
    open fun create(target: ActivityTarget, flags: Int = 0, requestCode: Int = 0,
                    context: Context? = contextProvider?.activityContext): Intent {

        val intent = Intent(context, target.targetClass)
        intent.addFlags(flags)

        val bundle = Bundle()
        target.parameters.forEach { it.addToBundle(bundle) }
        intent.putExtras(bundle)
        return intent
    }

    @JvmOverloads
    open fun create(target: FragmentTarget, containerId: Int = target.containerId,
                    addToBackStack: Boolean = true, clearBackStack: Boolean = false): Fragment {

        val bundle = Bundle()
        for (parameter in target.parameters) { parameter.addToBundle(bundle) }

        val fragment = target.fragment
        fragment.arguments = bundle

        return fragment
    }

    private fun showFragmentAsDefault(target: FragmentTarget, containerId: Int = target.containerId,
                                      addToBackStack: Boolean, clearBackStack: Boolean,
                                      bundle: Bundle, context: FragmentActivity) {
        val fragment = target.fragment
        fragment.arguments = bundle

        val ft = context.supportFragmentManager.beginTransaction()
        ft.replace(containerId, fragment, target.tag)

        if (clearBackStack) clearFragmentBackStack()
        if (addToBackStack) ft.addToBackStack(null)

        val enterAnimation = target.enterAnimation
        val exitAnimation = target.exitAnimation
        val popEnterAnimation = target.enterAnimation
        val popExitAnimation = target.exitAnimation

        if(enterAnimation != -1 && exitAnimation != -1 && popEnterAnimation != -1 && popExitAnimation != -1) {
            ft.setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)
        } else if(enterAnimation != -1 && exitAnimation != -1) ft.setCustomAnimations(enterAnimation, exitAnimation)

        ft.commit()
    }

    @JvmOverloads
    open fun executeIntent(intent: Intent, requestCode: Int = 0,
                           context: Context? = contextProvider?.activityContext) {

        if (context != null && context is Activity && requestCode > 0) {
            context.startActivityForResult(intent, requestCode)
        } else context?.startActivity(intent)
    }

    open fun clearFragmentBackStack() {
        val context = contextProvider?.activityContext
        if (context != null && context is FragmentActivity && context.supportFragmentManager.backStackEntryCount > 0) {
            context.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    open fun onBackPressed(enterAnimation: Int = -1, exitAnimation: Int = -1) {
        val activity = contextProvider?.activity
        activity?.onBackPressed()
        if(enterAnimation != -1 && exitAnimation != -1) {
            activity?.overridePendingTransition(enterAnimation, exitAnimation)
        }
    }

    open fun finish(enterAnimation: Int = -1, exitAnimation: Int = -1) {
        val activity = contextProvider?.activity
        activity?.finish()
        if(enterAnimation != -1 && exitAnimation != -1) {
            activity?.overridePendingTransition(enterAnimation, exitAnimation)
        }
    }

    @JvmOverloads
    open fun finishWithResult(resultCode: Int, data: Intent? = null,
                              enterAnimation: Int = -1, exitAnimation: Int = -1) {

        val activity = contextProvider?.activity
        if (activity != null) {
            activity.setResult(resultCode, data)
            activity.finish()

            if(enterAnimation != -1 && exitAnimation != -1) {
                activity.overridePendingTransition(enterAnimation, exitAnimation)
            }
        }
    }
}