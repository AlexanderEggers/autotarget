package org.demo.autointent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import org.autointent.R
import org.autointent.generated.ActivityService
import org.autointent.service.NavigationService
import org.autointent.util.ContextInjector

class MainActivity : AppCompatActivity() {

    private val navigationService: NavigationService = NavigationService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextInjector.inject(this)

        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            navigationService.navigate(ActivityService.showNextActivity("Test successful!"))
        }
    }
}