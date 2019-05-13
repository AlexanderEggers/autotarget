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
   def autotarget_version = "0.16.0"

  //Bundles all artifacts for the library including some additional helper classes
  implementation "org.autotarget:autotarget-core:$autotarget_version"

  //Only for annotation classes
  implementation "org.autotarget:autotarget-annotation:$autotarget_version"
  
  //requires autotarget-core to work
  kapt "org.autotarget:autotarget-processor:$autotarget_version"
}
```

How do I use AutoTarget? (Step-by-step introduction for 0.16.0)
-------------------

1. Add the annotations

Currently the library is only supporting activities and fragments. If you want to access a certain activity by another activity you have to use the @ActivityTarget annotation. Because activities sometimes require certain bundle values to show the right information, you can use the annotation @TargetParameter. This annotation allows you to define the required bundle values for this activity. The annotation expects an Array and therefore you can define n TargetParameterItem.

```kotlin

@ActivityTarget
@TargetParameter([TargetParameterItem(key = "MY_KEY", type = String::class, name = "myDemoValue")])
class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
    }
}
```

Fragments have a similar usage except you can to define certain optional values within the @FragmentTarget annotation. Those values are for the fragment container, the TAG and a state (which is only needed if you want to implement a custom solution in how fragments are created). This annotation allows you to define the required bundle values for this fragment. The annotation expects an Array and therefore you can define n TargetParameterItem.

```kotlin

@FragmentTarget(R.id.fragment_container)
@TargetParameter([TargetParameterItem(key = "MY_KEY", type = String::class, name = "myDemoValue")])
class DemoFragment : Fragment() {
     ...
}
```

The library is supporting two ways which will create and display fragments. You can either implement the HasFragmentFlow interface to your activity or rely on the internal implementation. The HasFragmentFlow interface allows you to implement your own solution in how fragments will be created. Due to this implementation you will need to set the state for your fragment within the @FragmentTarget annotation. The method onShowNextFragment is needed to be implemented using the HasFragmentFlow interface, needs a boolean as a return value. This values tells the underlying system if your implemention can handle show Fragment operation or not. This behavior can be handy if you want to quickly debug things without the need to fully implement everything.

The relevant TargetParameterItem annotation can have a key, the type of the parameter, a name and a group array. The parameter name is for the generated method to improve the readability for that specific value. The group array can be used to group parameters. Therefore your activity could have different entry points, like deeplinking, default etc. By default all parameters have the group array "[TargetParameterItem.DEFAULT_GROUP_KEY]".

```kotlin

@FragmentTarget(R.id.fragment_container)
@TargetParameter([TargetParameterItem(key = "MY_KEY", type = String::class, name = "myDemoValue", group = ["deeplinking", TargetParameterItem.DEFAULT_GROUP_KEY])])
class DemoFragment : Fragment() {
     ...
}
```

2. Call your target!

Before you can call your DemoActivity, you **have to** inject the current Context object by using the ContextProvider class which is provided by the library. This call simplifies the usage and should avoid dragging the Context reference to each and every part of your application (**if you are using the library [Archknife][3], you don't need to do this step**)! To call your DemoActivity, you need to create a reference of the TargetService class. You need to use one of the execute methods provided by this class. Activity based calls require a ActivityTarget object. This can be received by using the generated class ActivityTargets. This class holds all relevant methods to generate the needed ActivityTarget object for you.

```kotlin

class MainActivity : AppCompatActivity() {

    private val targetService: TargetService = TargetService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextProvider.context = this
        ...
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            targetService.execute(ActivityTargets.showNextActivity("Test successful!"))
        }
    }
}

```

Using Fragments, the implemention is quite similar. You also need to use the TargetService. To access the generated methods for your Fragments, you need to use the FragmentTargets class.

```kotlin

class MainActivity : AppCompatActivity() {

    private val targetService: TargetService = TargetService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextProvider.context = this
        ...
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            targetService.execute(FragmentsTarget.showDemoFragment())
        }
    }
}

```
TODO: Documentation about ActivityBundles/FragmentBundles.

Status
------
Version 1.0.0 is currently under development in the master branch.

Comments/bugs/questions/pull requests are always welcome!

Compatibility
-------------
 * AutoTarget requires at minimum Android 16.

Author
------
Alexander Eggers - [@mordag][2] on GitHub

License
-------
Apache 2.0. See the [LICENSE][1] file for details.


[1]: https://github.com/Mordag/autotarget/blob/master/LICENSE
[2]: https://github.com/Mordag
[3]: https://github.com/Mordag/archknife
