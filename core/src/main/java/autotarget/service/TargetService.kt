package autotarget.service

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.util.Log
import archknife.context.ContextProvider
import autotarget.util.HasFragmentFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class TargetService @Inject constructor() {

    private val contextProvider = ContextProvider

    @JvmOverloads
    open fun execute(target: ActivityTarget, flags: Int = 0, requestCode: Int = 0) {
        val intent = Intent(contextProvider.context, target.targetClass)
        intent.addFlags(flags)

        val bundle = Bundle()
        target.parameters.forEach {
            it.addToBundle(bundle)
        }
        intent.putExtras(bundle)

        val context = contextProvider.context
        if (context is Activity && requestCode > 0) {
            context.startActivityForResult(intent, requestCode)
        } else {
            context?.startActivity(intent)
        }
    }

    @SuppressLint("LogNotTimber")
    @JvmOverloads
    open fun execute(target: FragmentTarget, containerId: Int = target.containerId,
                addToBackStack: Boolean = true, clearBackStack: Boolean = false) {

        val bundle = Bundle()
        for (parameter in target.parameters) {
            parameter.addToBundle(bundle)
        }

        if (containerId == -1) {
            Log.e(TargetService::class.java.name, "Container ID cannot be -1. Check your " +
                    "annotation or set a custom container id using this method.")
        }

        val context = contextProvider.context
        if (context is HasFragmentFlow && containerId != -1 && target.state != -1) {
            val check = context.onShowNextFragment(containerId, target.state, addToBackStack, clearBackStack, bundle)

            if (!check && context is FragmentActivity) {
                showFragmentAsDefault(target, containerId, addToBackStack, clearBackStack, bundle, context)
            }
        } else if (context is FragmentActivity && containerId != -1) {
            showFragmentAsDefault(target, containerId, addToBackStack, clearBackStack, bundle, context)
        }
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

        ft.commit()
        context.supportFragmentManager.executePendingTransactions()
    }

    open fun clearFragmentBackStack() {
        val context = contextProvider.context
        if (context is FragmentActivity && context.supportFragmentManager.backStackEntryCount > 0) {
            context.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    open fun onBackPressed() {
        val context = contextProvider.context
        if (context is Activity) {
            context.onBackPressed()
        }
    }

    open fun finish() {
        val context = contextProvider.context
        if (context is Activity) {
            context.finish()
        }
    }

    @JvmOverloads
    open fun finishWithResult(resultCode: Int, data: Intent? = null) {
        val context = contextProvider.context
        if (context is Activity) {
            context.setResult(resultCode, data)
            context.finish()
        }
    }
}