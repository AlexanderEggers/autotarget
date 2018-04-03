package org.autointent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import org.autointent.generated.ActivityService
import org.autointent.generated.ContextInjector

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextInjector.inject(this)

        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            ActivityService().showNextActivity("Test successful!")
        }
    }
}