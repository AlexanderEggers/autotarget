package org.demo.autotarget

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import archknife.context.ContextProvider
import autotarget.annotation.ActivityTarget
import autotarget.generated.ActivityTargets
import autotarget.service.TargetService

@ActivityTarget
class MainActivity : AppCompatActivity() {

    private val targetService: TargetService = TargetService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextProvider.context = this

        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            targetService.execute(ActivityTargets.showJavaActivity())
        }
    }
}