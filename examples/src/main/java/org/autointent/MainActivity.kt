package org.autointent

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import org.autointent.annotation.ForActivity
import org.autointent.annotation.IntentParameter
import org.autointent.annotation.ParameterType
import org.autointent.generated.ActivityService
import org.autointent.generated.ContextInjector

@ForActivity
@IntentParameter(valueKey = "test", parameterType = ParameterType.ACTION, valueType = String::class)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        ContextInjector.inject(this)
        ActivityService().showMainActivity("test")
    }
}