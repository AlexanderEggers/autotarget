package org.autotarget.service

import org.autotarget.util.ContextInjector
import android.content.Intent
import android.app.Activity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import org.autotarget.util.HasFragmentFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TargetService @Inject constructor() {

    private val contextProvider = ContextInjector.contextProvider

    fun execute(target: ActivityTarget, flags: Int = 0, requestCode: Int = 0) {
        val intent = Intent(contextProvider.getContext(), target.targetClass)
        intent.addFlags(flags)

        val bundle = Bundle()
        target.parameters.forEach {
            it.addToBundle(bundle)
        }
        intent.putExtras(bundle)

        if (requestCode > 0) {
            (contextProvider.getContext() as Activity).startActivityForResult(intent, requestCode)
        } else {
            contextProvider.getContext()?.startActivity(intent)
        }
    }

    fun execute(target: FragmentTarget, addToBackStack: Boolean = true, clearBackStack: Boolean = false) {
        val bundle = Bundle()

        for (parameter in target.parameters) {
            parameter.addToBundle(bundle)
        }

        val context = contextProvider.getContext()
        if(target.state != -1 && context is HasFragmentFlow) {
            context.onShowNextFragment(target.state, addToBackStack, clearBackStack, bundle)
        } else if (context is FragmentActivity) {
            val fragment = target.fragment
            fragment.arguments = bundle

            val ft = context.supportFragmentManager.beginTransaction()
            ft.replace(target.containerId, fragment, target.tag)

            if(clearBackStack) clearFragmentBackStack()
            if (addToBackStack) ft.addToBackStack(null)

            ft.commit()
            context.supportFragmentManager.executePendingTransactions()
        }
    }

    fun clearFragmentBackStack() {
        val context = contextProvider.getContext()
        if (context is FragmentActivity && context.supportFragmentManager.backStackEntryCount > 0) {
            context.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    fun onBackPressed() {
        val context = contextProvider.getContext()
        if (context is Activity) {
            context.onBackPressed()
        }
    }

    fun finish() {
        val context = contextProvider.getContext()
        if (context is Activity) {
            context.finish()
        }
    }

    fun finishWithResult(resultCode: Int) {
        val context = contextProvider.getContext()
        if (context is Activity) {
            context.setResult(resultCode)
            context.finish()
        }
    }
}