package org.demo.autotarget

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import org.autotarget.annotation.ActivityTarget
import org.autotarget.generated.ActivityTargets.showNextActivity
import org.autotarget.service.IntentService
import org.autotarget.util.ContextInjector

@ActivityTarget
class MainActivity : AppCompatActivity() {

    private val intentService: IntentService = IntentService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextInjector.inject(this)

        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            intentService.navigate(showNextActivity("Test successful!"))
        }
    }
}