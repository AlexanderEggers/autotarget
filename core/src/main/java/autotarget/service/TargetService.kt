package autotarget.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import archknife.context.ContextProvider
import archtree.FragmentDispatcher
import autotarget.util.AutoTargetFragmentDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class TargetService @Inject constructor(private val contextProvider: ContextProvider) {

    @JvmOverloads
    open fun execute(target: ActivityTarget, flags: Int = 0, requestCode: Int = 0,
                     context: Context? = contextProvider.activityContext) {

        val intent = create(target, flags, requestCode)
        if (context != null) {
            if (context is Activity && requestCode > 0) context.startActivityForResult(intent, requestCode)
            else context.startActivity(intent)

            val enterAnimation = target.enterAnimation
            val exitAnimation = target.exitAnimation
            if (context is Activity && enterAnimation != -1 && exitAnimation != -1) {
                context.overridePendingTransition(target.enterAnimation, target.exitAnimation)
            }
        }
    }

    @JvmOverloads
    open fun execute(target: FragmentTarget, containerId: Int = target.containerId,
                     context: Context? = contextProvider.activityContext) {

        val bundle = Bundle()
        for (parameter in target.parameters) {
            parameter.addToBundle(bundle)
        }

        if (containerId == -1) {
            Log.e(TargetService::class.java.name, "Container ID cannot be -1. Check your " +
                    "annotation or set a custom container id using the execute method.")
        } else {
            var check = false
            if (context != null && context is FragmentDispatcher && target.state != -1) {
                check = context.showFragment(containerId, target.state, bundle)
            }

            if (!check && context != null && context is AutoTargetFragmentDispatcher && target.state != -1) {
                check = context.onShowNextFragment(target, target.state, containerId, bundle)
            }

            if (!check && context != null && context is FragmentActivity) {
                showFragmentAsDefault(target, containerId, bundle, context)
            }
        }
    }

    @JvmOverloads
    open fun create(target: ActivityTarget, flags: Int = 0, requestCode: Int = 0,
                    context: Context? = contextProvider.activityContext): Intent {

        val intent = Intent(context, target.targetClass)
        intent.addFlags(flags)

        val bundle = Bundle()
        target.parameters.forEach { it.addToBundle(bundle) }
        intent.putExtras(bundle)
        return intent
    }

    @JvmOverloads
    open fun create(target: FragmentTarget, containerId: Int = target.containerId): Fragment {

        val bundle = Bundle()
        for (parameter in target.parameters) {
            parameter.addToBundle(bundle)
        }

        val fragment = target.fragment
        fragment.arguments = bundle

        return fragment
    }

    private fun showFragmentAsDefault(target: FragmentTarget, containerId: Int = target.containerId,
                                      bundle: Bundle, context: FragmentActivity) {
        val fragment = target.fragment
        fragment.arguments = bundle

        val ft = context.supportFragmentManager.beginTransaction()
        ft.replace(containerId, fragment, target.tag)

        val enterAnimation = target.enterAnimation
        val exitAnimation = target.exitAnimation
        val popEnterAnimation = target.popEnterAnimation
        val popExitAnimation = target.popExitAnimation

        if (enterAnimation != -1 && exitAnimation != -1 && popEnterAnimation != -1 && popExitAnimation != -1) {
            ft.setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)
        } else if (enterAnimation != -1 && exitAnimation != -1) ft.setCustomAnimations(enterAnimation, exitAnimation)

        ft.commit()
    }

    @JvmOverloads
    open fun executeIntent(intent: Intent, requestCode: Int = 0,
                           context: Context? = contextProvider.activityContext,
                           enterAnimation: Int = -1, exitAnimation: Int = -1) {

        if (context != null && context is Activity && requestCode > 0) {
            context.startActivityForResult(intent, requestCode)
        } else context?.startActivity(intent)

        if (context is Activity && enterAnimation != -1 && exitAnimation != -1) {
            context.overridePendingTransition(enterAnimation, exitAnimation)
        }
    }

    open fun clearFragmentBackStack() {
        val context = contextProvider.activityContext
        if (context != null && context is FragmentActivity && context.supportFragmentManager.backStackEntryCount > 0) {
            context.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    @JvmOverloads
    open fun onBackPressed(enterAnimation: Int = -1, exitAnimation: Int = -1) {
        val activity = contextProvider.activity
        activity?.onBackPressed()
        if (enterAnimation != -1 && exitAnimation != -1) {
            activity?.overridePendingTransition(enterAnimation, exitAnimation)
        }
    }

    @JvmOverloads
    open fun finish(enterAnimation: Int = -1, exitAnimation: Int = -1) {
        val activity = contextProvider.activity
        activity?.finish()
        if (enterAnimation != -1 && exitAnimation != -1) {
            activity?.overridePendingTransition(enterAnimation, exitAnimation)
        }
    }

    @JvmOverloads
    open fun finishWithResult(resultCode: Int, data: Intent? = null,
                              enterAnimation: Int = -1, exitAnimation: Int = -1) {

        val activity = contextProvider.activity
        if (activity != null) {
            activity.setResult(resultCode, data)
            activity.finish()

            if (enterAnimation != -1 && exitAnimation != -1) {
                activity.overridePendingTransition(enterAnimation, exitAnimation)
            }
        }
    }
}