package org.demo.autotarget

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import autotarget.annotation.ActivityTarget
import autotarget.annotation.TargetParameter
import autotarget.annotation.TargetParameterItem
import autotarget.generated.ActivityBundles
import org.demo.autotarget.NextActivity.Companion.MY_KEY

@ActivityTarget
@TargetParameter([
    TargetParameterItem(key = MY_KEY, type = String::class, name = "text", required = true),
    TargetParameterItem(key = "testKey", type = Int::class, name = "intTest", group = ["test"], required = false)
], true)
class NextActivity : AppCompatActivity() {

    companion object {
        const val MY_KEY: String = "TEXT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)

        val textView = findViewById<TextView>(R.id.textView)

        val model = ActivityBundles.getNextActivityBundleModel(intent.extras)
        textView.text = model.text
    }
}