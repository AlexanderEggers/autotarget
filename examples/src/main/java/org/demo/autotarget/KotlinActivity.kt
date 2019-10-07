package org.demo.autotarget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import autotarget.annotation.ActivityTarget
import autotarget.generated.ActivityTargets
import autotarget.service.TargetService

@ActivityTarget
class KotlinActivity : AppCompatActivity() {

    private val targetService: TargetService = TargetService(ContextProviderSingleton.contextProvider)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextProviderSingleton.contextProvider.activityContext = this

        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            targetService.execute(ActivityTargets.showJavaActivity())
        }
    }
}