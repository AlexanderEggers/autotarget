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
        createIntent(target, flags)?.let { intent ->
            executeIntent(intent, requestCode, target.enterAnim, target.exitAnim)
        }
    }

    /**
     * Method that requires an [ActivityTarget] and uses the given flags create an [Intent].
     *
     * @param target object that includes the [Intent] extras
     * @param flags adds flags to the [Intent]
     *
     * @return [Intent]
     *
     * @since 1.0.0
     */
    @JvmOverloads
    open fun createIntent(target: ActivityTarget, flags: Int = 0): Intent? {
        return contextProvider.activityContext?.let { context ->
            Intent(context, target.targetClass).apply {
                addFlags(flags)
                putExtras(target.bundle)
            }
        }
    }

    /**
     * Method that requires an [Intent] and uses the given flags and animation values to
     * customize the starting of new [Activity] instance.
     *
     * @param intent will be used to start the new [Activity] instance
     * @param requestCode If >= 0, this code will be returned in onActivityResult() when the
     * [Activity] exits.
     * @param enterAnim A resource ID of the animation resource to use for the new [Activity]. Use
     * 0 for no animation.
     * @param exitAnim A resource ID of the animation resource to use for the current [Activity].
     * Use 0 for no animation.
     *
     * @since 1.0.0
     */
    @JvmOverloads
    open fun executeIntent(intent: Intent, requestCode: Int = 0, enterAnim: Int = -1,
                           exitAnim: Int = -1) {
        contextProvider.activity?.run {
            if (requestCode > 0) startActivityForResult(intent, requestCode)
            else startActivity(intent)

            if (enterAnim != -1 && exitAnim != -1) overridePendingTransition(enterAnim, exitAnim)
        }
    }

    /**
     * Method that requires an [FragmentTarget] to show a new [Fragment]. This method will either
     * use a default implementation to show a new [Fragment] or is using the current [Activity] if
     * it implements the [FragmentDispatcher] interface.
     *
     * @param target object that includes the [Fragment] instance, parameters and animation related
     * values
     *
     * @since 1.0.0
     */
    open fun execute(target: FragmentTarget) {

        if (target.containerId == -1) {
            Log.e(TargetService::class.java.name, "Container ID cannot be -1. Check your " +
                    "FragmentTarget annotation.")
            return
        }

        contextProvider.activity?.let { activity ->
            var check = false

            if (activity is FragmentDispatcher) {
                check = activity.showFragment(target)
            }
            if (!check && activity is FragmentActivity) {
                executeFragment(target, activity)
            }
        }
    }

    /**
     * Method that requires an [FragmentTarget] and [FragmentActivity] to show a new [Fragment].
     *
     * @param target object that includes the [Fragment] instance, parameters and animation related
     * values
     * @param activity used to show the new [Fragment]
     *
     * @since 1.0.0
     */
    open fun executeFragment(target: FragmentTarget, activity: FragmentActivity) {
        val fragment = target.fragment

        val ft = activity.supportFragmentManager.beginTransaction()
        ft.replace(target.containerId, fragment, target.tag)

        val enterAnim = target.enterAnim
        val exitAnim = target.exitAnim
        val popEnterAnim = target.popEnterAnim
        val popExitAnim = target.popExitAnim

        if (enterAnim != -1 && exitAnim != -1 && popEnterAnim != -1 && popExitAnim != -1) {
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        } else if (enterAnim != -1 && exitAnim != -1) {
            ft.setCustomAnimations(enterAnim, exitAnim)
        }

        ft.commit()
    }

    /**
     * @param name If non-null, this is the name of a previous back state to look for; if found, all
     * states up to that state will be popped.
     */
    @JvmOverloads
    open fun clearFragmentBackStack(name: String? = null, hasNavHostFragment: Boolean = false) {
        contextProvider.activity?.run {
            if (this@run is FragmentActivity) {
                val fragmentManager = if (hasNavHostFragment) {
                    val navHostFragment = supportFragmentManager.fragments.firstOrNull() as? NavHostFragment?
                    navHostFragment?.childFragmentManager
                } else supportFragmentManager

                fragmentManager?.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }

    @JvmOverloads
    open fun onBackPressed(enterAnim: Int = -1, exitAnim: Int = -1) {
        contextProvider.activity?.run {
            onBackPressed()
            if (enterAnim != -1 && exitAnim != -1) overridePendingTransition(enterAnim, exitAnim)
        }
    }

    @JvmOverloads
    open fun finish(resultCode: Int = 0, data: Intent? = null, enterAnim: Int = -1, exitAnim: Int = -1) {
        contextProvider.activity?.run {
            if (resultCode > 0) setResult(resultCode, data)
            finish()
            if (enterAnim != -1 && exitAnim != -1) overridePendingTransition(enterAnim, exitAnim)
        }
    }
}