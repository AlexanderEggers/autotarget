package org.demo.autotarget

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import org.autotarget.annotation.ActivityTarget
import org.autotarget.annotation.TargetParameter
import org.demo.autotarget.NextActivity.Companion.MY_KEY

@ActivityTarget
@TargetParameter(key = MY_KEY, type = String::class, name = "text")
class NextActivity : AppCompatActivity() {

    companion object {
        const val MY_KEY: String = "TEXT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = intent.extras.getString(MY_KEY)
    }
}