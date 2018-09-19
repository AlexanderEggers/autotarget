package org.demo.autotarget

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import autotarget.annotation.ActivityTarget
import autotarget.annotation.TargetParameter
import autotarget.annotation.TargetParameterItem
import org.demo.autotarget.NextActivity.Companion.MY_KEY

@ActivityTarget
@TargetParameter([TargetParameterItem(key = MY_KEY, type = String::class, name = "text"),
    TargetParameterItem(key = "test1", type = Int::class, name = "text1", optional = true)])
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