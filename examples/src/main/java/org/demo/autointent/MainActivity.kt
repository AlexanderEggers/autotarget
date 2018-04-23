package org.demo.autointent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import org.autointent.R
import org.autointent.annotation.ActivityTarget
import org.autointent.generated.ActivityTargets.showNextActivity
import org.autointent.service.IntentService
import org.autointent.util.ContextInjector

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