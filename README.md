AutoIntent
=====
[![Download](https://api.bintray.com/packages/mordag/android/autointent-processor/images/download.svg) ](https://bintray.com/mordag/android/autointent-processor/_latestVersion)

AutoIntent is an annotation processor to generate a service which can be used to handle your app navigation and intent usage.

Download
--------
You can use Gradle to download this libray:

```gradle
repositories {
  jcenter()
}

dependencies {
  implementation 'org.autointent:autointent-annotation:0.1.0'
  kapt 'org.autointent:autointent-processor:0.1.0'
}
```

How do I use AutoIntent? (Step-by-step introduction for 0.1.0)
-------------------

1. Add the annotations

Currently the library is only supporting activity intents. If you want to access a certain activity by another activity you have to use the @ForActivity annotation. Because activities sometimes require certain bundle values to show the right information, you can use the annotation @IntentParameter. This annotation allows you to define the required bundle values for this activity. Each activity can have n IntentParameter.

```kotlin

@ForActivity
@IntentParameter(valueKey = "MY_KEY", valueType = String::class, valueName = "myDemoValue")
class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
    }
}

```

2. Call your activity by another activity!

Before you can call your DemoActivity, you **have to** inject the current Context object by using the ContextInjector class which is provided by the library. This call simplifies the usage and should avoid dragging the Context reference to each and every part of your application! To call your DemoActivity, you need to create a reference of the ActivityService. This class holds the generated methods that can be used to call the DemoActivity.

```kotlin

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextInjector.inject(this)
        ...
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            ActivityService().showDemoActivity("Test successful!")
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

 * AutoIntent requires at minimum Java 7 or Android 2.3.
 
TODO
-------------
* Annotation processor: @ForFragment, @ForBroadcast, @ForService
* Unit testing
* Wiki documentation

Author
------
Alexander Eggers - [@mordag][2] on GitHub

License
-------
Apache 2.0. See the [LICENSE][1] file for details.


[1]: https://github.com/Mordag/autointent/blob/1.0/LICENSE
[2]: https://github.com/Mordag
