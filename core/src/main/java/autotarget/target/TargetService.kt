package autotarget.target

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import archknife.context.ContextProvider
import autotarget.util.FragmentDispatcher
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the library main class and handles all the different target object requests.
 *
 * @param contextProvider Class that is providing the context for showing [Fragment] and [Activity]
 * targets.
 *
 * @since 1.0.0
 */
@Singleton
open class TargetService @Inject constructor(private val contextProvider: ContextProvider) {

    /**
     * Method that requires an [ActivityTarget] and uses the given flags and requestCode to
     * customize the target [Activity] instance.
     *
     * @param target object that defines the target class, parameter and animation
     * @param flags adds flags to the [Intent]
     * @param requestCode If >= 0, this code will be returned in onActivityResult() when the
     * [Activity] exits.
     *
     * @since 1.0.0
     */
    @JvmOverloads
    open fun execute(target: ActivityTarget, flags: Int = 0, requestCode: Int = 0) {
        val intent = createIntent(target, flags)
        executeIntent(intent, requestCode, target.enterAnimation, target.exitAnimation)
    }

    @JvmOverloads
    open fun createIntent(target: ActivityTarget, flags: Int = 0): Intent {
        val context = contextProvider.activityContext
        return Intent(context, target.targetClass).apply {
            addFlags(flags)
            putExtras(target.bundle)
        }
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

    /**
     * Method that requires an [FragmentTarget] to customize the target [Fragment] instance.
     *
     * @param target object that defines the target class, parameter and animation
     */
    open fun execute(target: FragmentTarget) {

        if (target.containerId == -1) {
            Log.e(TargetService::class.java.name, "Container ID cannot be -1. Check your " +
                    "FragmentTarget annotation")
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

    private fun showFragmentAsDefault(target: FragmentTarget, context: FragmentActivity) {
        val fragment = target.fragment

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

    /**
     * @param name If non-null, this is the name of a previous back state to look for; if found, all
     * states up to that state will be popped.
     */
    @JvmOverloads
    open fun clearFragmentBackStack(name: String? = null, hasNavHostFragment: Boolean = false) {
        val context = contextProvider.activityContext
        if (context is FragmentActivity) {
            val fragmentManager = if (hasNavHostFragment) {
                val navHostFragment = context.supportFragmentManager.fragments.firstOrNull() as? NavHostFragment?
                navHostFragment?.childFragmentManager
            } else context.supportFragmentManager

            fragmentManager?.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
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