package org.autotarget.service

import org.autotarget.util.ContextInjector
import android.content.Intent
import android.app.Activity
import android.os.Bundle
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TargetService @Inject constructor() {

    private val contextProvider = ContextInjector.contextProvider

    fun navigate(target: ActivityTarget) {
        navigate(target, 0, 0)
    }

    fun navigate(target: ActivityTarget, flags: Int) {
        navigate(target, flags, 0)
    }

    fun navigate(target: ActivityTarget, flags: Int, requestCode: Int) {
        performNavigation(target.targetClass, requestCode, flags, target.parameters)
    }

    private fun performNavigation(target: Class<out Activity>, requestCode: Int, flags: Int, parameters: Array<ParameterProvider>) {
        val intent = Intent(contextProvider.getContext(), target)
        intent.addFlags(flags)

        val bundle = Bundle()
        parameters.forEach {
            it.addToBundle(bundle)
        }
        intent.putExtras(bundle)

        if (requestCode > 0) {
            (contextProvider.getContext() as Activity).startActivityForResult(intent, requestCode)
        } else {
            contextProvider.getContext()?.startActivity(intent)
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