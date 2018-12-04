package org.demo.autotarget

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import autotarget.annotation.FragmentTarget
import autotarget.generated.ActivityTargets
import autotarget.service.TargetService

@FragmentTarget(R.id.fragment_container)
class FragmentDemo : Fragment() {

    private val targetService = TargetService()
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_demo, container, false)

        val button = view.findViewById<Button>(R.id.fragment_button)
        button.setOnClickListener {
            targetService.execute(ActivityTargets.showNextActivityWithDefault("Test successful!"))
        }

        return view
    }
}