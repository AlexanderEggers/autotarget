AutoTarget
=====
[![Download](https://api.bintray.com/packages/mordag/android/autotarget-core/images/download.svg) ](https://bintray.com/mordag/android/autotarget-core/_latestVersion)

AutoTarget is an annotation processor to generate a service which can be used to handle your app navigation.

Download
--------
You can use Gradle to download this libray:

```gradle
repositories {
  jcenter()
}

dependencies {
  //Bundles all artifacts for the library including some additional helper classes
  implementation 'org.autotarget:autotarget-core:0.5.0'

  //Only for annotation classes
  implementation 'org.autotarget:autotarget-annotation:0.5.0'
  //Only for fragment helper class
  implementation 'org.autotarget:autotarget-fragment:0.5.0'
  
  //requires autotarget-core to work
  kapt 'org.autotarget:autotarget-processor:0.5.0'
}
```

How do I use AutoTarget? (Step-by-step introduction for 0.5.0)
-------------------

1. Add the annotations

Currently the library is only supporting activities and fragments. If you want to access a certain activity by another activity you have to use the @ActivityTarget annotation. Because activities sometimes require certain bundle values to show the right information, you can use the annotation @TargetParameter. This annotation allows you to define the required bundle values for this activity. Each activity can have n TargetParameter.

```kotlin

@ActivityTarget
@TargetParameter(key = "MY_KEY", type = String::class, name = "myDemoValue")
class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
    }
}
```

Fragments have a similar usage except you can to define certain optional values within the @FragmentTarget annotation. Those values are for the fragment container, the TAG and a state (which is only needed if you want to implement a custom solution in how fragments are created). Each fragment, like activities, can have n TargetParameter. 

```kotlin

@FragmentTarget(R.id.fragment_container)
@TargetParameter(key = "MY_KEY", type = String::class, name = "myDemoValue")
class DemoFragment : Fragment() {
     ...
}
```

The library is supporting two ways which will create and display fragments. You can either implement the HasFragmentFlow interface to your activity or rely on the internal implementation. The HasFragmentFlow interface allows you to implement your own solution in how fragments will be created. Due to this implementation you will need to set the state for your fragment within the @FragmentTarget annotation. The method onShowNextFragment is needed to be implemented using the HasFragmentFlow interface, needs a boolean as a return value. This values tells the underlying system if your implemention can handle show Fragment operation or not. This behavior can be handy if you want to quickly debug things without the need to fully implement everything.

2. Call your target!

Before you can call your DemoActivity, you **have to** inject the current Context object by using the ContextInjector class which is provided by the library. This call simplifies the usage and should avoid dragging the Context reference to each and every part of your application! To call your DemoActivity, you need to create a reference of the TargetService class. **Please note: If you want to access the TargetService via Java-based source code, it is highly recommended to use the JTargetService class instead. This class is optimized for the usage within Java classes.** Regardless if you are going to use the TargetService or the JTargetService you need to use one of the execute methods provided by this class. Activity based calls require a ActivityTarget object. This can be received by using the generated class ActivityTargets. This class holds all relevant methods to generate the needed ActivityTarget object for you.

```kotlin

class MainActivity : AppCompatActivity() {

    private val targetService: TargetService = TargetService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextInjector.inject(this)
        ...
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            targetService.execute(ActivityTargets.showNextActivity("Test successful!"))
        }
    }
}

```

Using Fragments, the implemention is quite similar. You also need to use the TargetService or JTargetService. To access the generated methods for your Fragments, you need to use the FragmentTargets class.

```kotlin

class MainActivity : AppCompatActivity() {

    private val targetService: TargetService = TargetService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextInjector.inject(this)
        ...
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            targetService.execute(FragmentsTarget.showDemoFragment())
        }
    }
}

```

Status
------
Version 1.0.0 is currently under development in the master branch.

Comments/bugs/questions/pull requests are always welcome!

Compatibility
-------------
 * AutoTarget requires at minimum Android 14.

Author
------
Alexander Eggers - [@mordag][2] on GitHub

License
-------
Apache 2.0. See the [LICENSE][1] file for details.


[1]: https://github.com/Mordag/autotarget/blob/master/LICENSE
[2]: https://github.com/Mordag
