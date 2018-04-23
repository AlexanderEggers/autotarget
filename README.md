AutoTarget
=====
[![Download](https://api.bintray.com/packages/mordag/android/autotarget-core/images/download.svg) ](https://bintray.com/mordag/android/autotarget-core/_latestVersion)

AutoTarget is an annotation processor to generate a service which can be used to handle your app navigation and intent usage.

Download
--------
You can use Gradle to download this libray:

```gradle
repositories {
  jcenter()
}

dependencies {
  //Only for annotation classes
  implementation 'org.autointent:autointent-annotation:0.2.0'
  //For annotation and helper classes
  implementation 'org.autointent:autointent-core:0.2.0'
  
  //requires autointent-core to work
  kapt 'org.autointent:autointent-processor:0.2.0'
}
```

How do I use AutoTarget? (Step-by-step introduction for 0.2.0)
-------------------

1. Add the annotations

Currently the library is only supporting activity intents. If you want to access a certain activity by another activity you have to use the @ActivityTarget annotation. Because activities sometimes require certain bundle values to show the right information, you can use the annotation @TargetParameter. This annotation allows you to define the required bundle values for this activity. Each activity can have n TargetParameter.

```kotlin

@ForActivity
@IntentParameter(key = "MY_KEY", type = String::class, name = "myDemoValue")
class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
    }
}

```

2. Call your activity by another activity!

Before you can call your DemoActivity, you **have to** inject the current Context object by using the ContextInjector class which is provided by the library. This call simplifies the usage and should avoid dragging the Context reference to each and every part of your application! To call your DemoActivity, you need to create a reference of the IntentService (not the final name!) class. You need to use one of the navigate methods provided by this class. Each call requires a ActivityTarget object. This can be received by using the generated class ActivityTargets. This class holds all relevant methods to generate the needed ActivityTarget object for you.

```kotlin

class MainActivity : AppCompatActivity() {

    private val intentService: IntentService = IntentService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextInjector.inject(this)
        ...
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            intentService.navigate(ActivityTargets.showNextActivity("Test successful!"))
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

 * AutoTarget requires at minimum Android 16.
 
TODO
-------------
* Annotation processor: @FragmentTarget and maybe also @BroadcastTarget, @ServiceTarget
* Unit testing
* Wiki documentation

Author
------
Alexander Eggers - [@mordag][2] on GitHub

License
-------
Apache 2.0. See the [LICENSE][1] file for details.


[1]: https://github.com/Mordag/autotarget/blob/master/LICENSE
[2]: https://github.com/Mordag
