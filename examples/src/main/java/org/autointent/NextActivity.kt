package org.autointent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import org.autointent.annotation.ForActivityIntent
import org.autointent.annotation.IntentParameter

@ForActivityIntent
@IntentParameter(valueKey = "TEXT_KEY", valueType = String::class, valueName = "text")
class NextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = intent.extras.getString("TEXT_KEY")
    }
}