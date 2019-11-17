package autotarget.target

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import archknife.context.ContextProvider
import autotarget.util.FragmentDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class TargetService @Inject constructor(private val contextProvider: ContextProvider) {

    @JvmOverloads
    open fun execute(target: ActivityTarget, flags: Int = 0, requestCode: Int = 0) {
        val context = contextProvider.activityContext
        val intent = create(target, flags)

        if (context is Activity) {
            if (requestCode > 0) context.startActivityForResult(intent, requestCode)
            else context.startActivity(intent)

            val enterAnimation = target.enterAnimation
            val exitAnimation = target.exitAnimation
            if (enterAnimation != -1 && exitAnimation != -1) {
                context.overridePendingTransition(target.enterAnimation, target.exitAnimation)
            }
        }
    }

    open fun execute(target: FragmentTarget) {

        if (target.containerId == -1) {
            Log.e(TargetService::class.java.name, "Container ID cannot be -1. Check your " +
                    "annotation or set a custom container id using the execute method.")
            return
        }

        val context = contextProvider.activityContext
        var check = false

        if (context is FragmentDispatcher) {
            check = context.showFragment(target)
        }
        if (!check && context is FragmentActivity) {
            showFragmentAsDefault(target, context)
        }
    }

    @JvmOverloads
    open fun create(target: ActivityTarget, flags: Int = 0): Intent {
        val context = contextProvider.activityContext
        return Intent(context, target.targetClass).apply {
            addFlags(flags)
            putExtras(target.bundle)
        }
    }

    open fun create(target: FragmentTarget): Fragment {
        return target.fragment.apply { arguments = target.bundle }
    }

    private fun showFragmentAsDefault(target: FragmentTarget, context: FragmentActivity) {
        val fragment = target.fragment
        fragment.arguments = target.bundle

        val ft = context.supportFragmentManager.beginTransaction()
        ft.replace(target.containerId, fragment, target.tag)

        val enterAnimation = target.enterAnimation
        val exitAnimation = target.exitAnimation
        val popEnterAnimation = target.popEnterAnimation
        val popExitAnimation = target.popExitAnimation

        if (enterAnimation != -1 && exitAnimation != -1 && popEnterAnimation != -1 && popExitAnimation != -1) {
            ft.setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)
        } else if (enterAnimation != -1 && exitAnimation != -1) {
            ft.setCustomAnimations(enterAnimation, exitAnimation)
        }

        ft.commit()
    }

    @JvmOverloads
    open fun executeIntent(intent: Intent, requestCode: Int = 0, enterAnimation: Int = -1,
                           exitAnimation: Int = -1) {
        val context = contextProvider.activityContext
        if (context is Activity) {
            if (requestCode > 0) context.startActivityForResult(intent, requestCode)
            else context.startActivity(intent)

            if (enterAnimation != -1 && exitAnimation != -1) {
                context.overridePendingTransition(enterAnimation, exitAnimation)
            }
        }
    }

    open fun clearFragmentBackStack() {
        val context = contextProvider.activityContext
        if (context is FragmentActivity && context.supportFragmentManager.backStackEntryCount > 0) {
            context.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    @JvmOverloads
    open fun onBackPressed(enterAnimation: Int = -1, exitAnimation: Int = -1) {
        val context = contextProvider.activityContext
        if (context is Activity) {
            context.onBackPressed()

            if (enterAnimation != -1 && exitAnimation != -1) {
                context.overridePendingTransition(enterAnimation, exitAnimation)
            }
        }
    }

    @JvmOverloads
    open fun finish(enterAnimation: Int = -1, exitAnimation: Int = -1) {
        val context = contextProvider.activityContext
        if (context is Activity) {
            context.finish()

            if (enterAnimation != -1 && exitAnimation != -1) {
                context.overridePendingTransition(enterAnimation, exitAnimation)
            }
        }
    }

    @JvmOverloads
    open fun finishWithResult(resultCode: Int, data: Intent? = null,
                              enterAnimation: Int = -1, exitAnimation: Int = -1) {

        val context = contextProvider.activityContext
        if (context is Activity) {
            context.setResult(resultCode, data)
            context.finish()

            if (enterAnimation != -1 && exitAnimation != -1) {
                context.overridePendingTransition(enterAnimation, exitAnimation)
            }
        }
    }
}